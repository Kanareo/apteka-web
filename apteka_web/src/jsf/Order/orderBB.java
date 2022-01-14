package jsf.Order;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import jsf.dao.OrderDAO;
import jsf.dao.OrderItemDAO;
import jsf.dao.UserDAO;
import jsf.entities.Order;
import jsf.entities.OrderItem;
import jsf.entities.Product;
import jsf.entities.User;

@Named
@SessionScoped

public class orderBB implements Serializable{
	private static final long serialVersionUID = 1L;

	private static final String PAGE_STAY_AT_THE_SAME = null;
	private static final String PAGE_PRODUCT_LIST = "pages/apteka/browser";
	private static final String PAGE_ORDER_EDIT = "pages/apteka/orderView";
	
	private Order order;
	private OrderItem orderItem;
	private LazyDataModel<Order> orderList;
	
	private String email;
	
	private int itemQuantity = 0;
	private float orderPrice = 0;
	
	private List<OrderItem> orderItems;
	
	@Inject
	FacesContext ctx;
	
	@EJB
	OrderDAO orderDAO;
	
	@EJB
	OrderItemDAO orderItemDAO;
	
	@EJB
	UserDAO userDAO;
	
	@PostConstruct
	public void init() {
		orderList = new LazyDataModel<Order>() {
			private static final long serialVersionUID = 1L;

			private List<Order> orders;		
			
			@Override
			public Order getRowData(String rowKey) {
				for (Order order : orders) {
					if (order.getIdOrder() == Integer.parseInt(rowKey)) {
						return order;
					}
				}
				return null;
			}

			@Override
			public String getRowKey(Order order) {
				return String.valueOf(order.getIdOrder());
			}

			@Override
			public List<Order> load(int offset, int pageSize, Map<String, SortMeta> sortBy,
					Map<String, FilterMeta> filterBy) {
				
				orders = orderDAO.getLazyList(null, offset, pageSize);
				int rowCount = (int) orderDAO.countLazyList(null);
				setRowCount(rowCount);

				return orders;
			}
		};
	}
	
	public String addItem(Product product) {
		//orderDAO.getLastID();
		if(product != null) {
			if(orderItems == null) {
				orderItems = new ArrayList<OrderItem>();
			}
			orderItem = new OrderItem();
			orderItem.setProduct(product);
			orderItem.setQuantity(itemQuantity);
			orderItem.setCombinedPrice((float)Math.round(((product.getProductPrice()*itemQuantity))*100f)/100f);
			orderItem.setDiscount(0);
			orderItems.add(orderItem);
			orderPrice += (float)Math.round(((orderItem.getCombinedPrice()))*100f)/100f;
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Dodano " + orderItem.getProduct().getProductName() , null));
		}
		return PAGE_STAY_AT_THE_SAME;
	}
	
	public void deleteItem(OrderItem forwardedItem) {
		orderItems.remove(forwardedItem);
		orderPrice -= forwardedItem.getCombinedPrice();
		if(orderItems.isEmpty()) {
			orderItems = null;
		}
	}
	
	public String addOrder() {
		int i = 0;
		
		try {
			if (!orderItems.isEmpty()) {
				
				order = new Order();
				order.setOrderDate(java.sql.Date.valueOf(java.time.LocalDate.now()));
				order.setDeliveryDate(null);
				order.setOrderStatus("Zamówione");
				order.setUser(userDAO.find(userDAO.getUser(email).getIdUser()));
//				System.out.println(order.getOrderStatus());
//				System.out.println(order.getDeliveryDate());
//				System.out.println(order.getOrderDate());
//				System.out.println(order.getIdOrder());
//				System.out.println(order.getUser().getIdUser());
				orderDAO.create(order);
				while(i <= orderItems.size()-1) {
					orderItem = orderItems.get(i);
					orderItem.setOrder(order);
					orderItemDAO.create(orderItem);
					i++;
				}
				orderPrice = 0;
				orderItems = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wystąpił błąd podczas zapisu", null));
			return PAGE_STAY_AT_THE_SAME;
		}
		return PAGE_STAY_AT_THE_SAME;
	}
	
	public boolean showDelivery() {
		return orderDAO.showDelivery();
	}

	public int getItemQuantity() {
		return itemQuantity;
	}

	public void setItemQuantity(int itemQuantity) {
		this.itemQuantity = itemQuantity;
	}

	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	public float getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(float orderPrice) {
		this.orderPrice = orderPrice;
	}

	public LazyDataModel<Order> getOrderList() {
		return orderList;
	}

	public void setOrderList(LazyDataModel<Order> orderList) {
		this.orderList = orderList;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
