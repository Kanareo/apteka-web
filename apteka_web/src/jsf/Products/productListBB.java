package jsf.Products;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import jsf.dao.ProductDAO;
import jsf.entities.Product;

@Named
@ViewScoped
public class productListBB implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAGE_STAY_AT_THE_SAME = null;	
	
	private LazyDataModel<Product> lazyProducts;
	private Product selectedProduct;
	
	private String productName;
	private int brandName;
	private int categoryName;
	
	@Inject
	FacesContext ctx;
	@Inject
	Flash flash;

	@EJB
	ProductDAO productDAO;
	
	@PostConstruct
	public void init() {
		lazyProducts = new LazyDataModel<Product>() {
			private static final long serialVersionUID = 1L;

			private List<Product> products;
			
			Map<String, Object> filter = new HashMap<String, Object>();

			@Override
			public Product getRowData(String rowKey) {
				for (Product product : products) {
					if (product.getIdProduct() == Integer.parseInt(rowKey)) {
						return product;
					}
				}
				return null;
			}

			@Override
			public String getRowKey(Product product) {
				return String.valueOf(product.getIdProduct());
			}

			@Override
			public List<Product> load(int offset, int pageSize, Map<String, SortMeta> sortBy,
					Map<String, FilterMeta> filterBy) {
				
				filter.clear();
				if(productName != null && productName.length()> 0 ) {
					filter.put("productName", productName);
				}
				filter.put("brandName", brandName);
				filter.put("categoryName", categoryName);
				
				products = productDAO.getLazyList(filter, offset, pageSize);
				int rowCount = (int) productDAO.countLazyList(filter);
				setRowCount(rowCount);

				return products;
			}
		};	
		
	}
	
	public void clearFilter() {
		productName = null;
		brandName = 0;
	}
	
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getBrandName() {
		return brandName;
	}

	public void setBrandName(int brandName) {
		this.brandName = brandName;
	}

	public int getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(int categoryName) {
		this.categoryName = categoryName;
	}

	public LazyDataModel<Product> getLazyProducts() {
		return lazyProducts;
	}

	public void setLazyProducts(LazyDataModel<Product> lazyProducts) {
		this.lazyProducts = lazyProducts;
	}

	public Product getSelectedProduct() {
		return selectedProduct;
	}

	public void setSelectedProduct(Product selectedProduct) {
		this.selectedProduct = selectedProduct;
	}

	public void onRowSelect(SelectEvent<Product> event) {
		FacesMessage msg = new FacesMessage("Wybrana marka", String.valueOf(event.getObject().getIdProduct()));
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public List<Product> getFullList() {
		return productDAO.getFullList();
	}
	
	public String editQuantity() {
		if(selectedProduct != null) {
			productDAO.merge(selectedProduct);
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Pomyślnie zmieniono ilość produktu dla produktu o nazwie " + selectedProduct.getProductName(), null));
		}else ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Zmiana ilości nie udała się, spróbuj ponownie", null));
		return PAGE_STAY_AT_THE_SAME;
	}
	
}
