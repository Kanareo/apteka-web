package jsf.User;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.Flash;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import jsf.dao.UserDAO;
import jsf.entities.User;

@Named
@ViewScoped
public class userListBB implements Serializable{
	private static final long serialVersionUID = 1L;

	private static final String PAGE_STAY_AT_THE_SAME = null;
	
	private LazyDataModel<User> lazyUsers;
	private User selectedUser;
	
	private User user = new User();
	
	@Inject
	Flash flash;

	@EJB
	UserDAO userDAO;
	
	@PostConstruct
	public void init() {
		user.setRole("all");
		user.setBlocked((byte)2);
		lazyUsers = new LazyDataModel<User>() {
			private static final long serialVersionUID = 1L;

			private List<User> users;
			
			Map<String, Object> filter = new HashMap<String, Object>();

			@Override
			public User getRowData(String rowKey) {
				for (User user : users) {
					if (user.getIdUser() == Integer.parseInt(rowKey)) {
						return user;
					}
				}
				return null;
			}

			@Override
			public String getRowKey(User user) {
				return String.valueOf(user.getIdUser());
			}

			@Override
			public List<User> load(int offset, int pageSize, Map<String, SortMeta> sortBy,
					Map<String, FilterMeta> filterBy) {
				
				filter.clear();
				filter.put("user", user);
				
				users = userDAO.getLazyList(filter, offset, pageSize);
				int rowCount = (int) userDAO.countLazyList(filter);
				setRowCount(rowCount);

				return users;
			}
		};
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(User selectedUser) {
		this.selectedUser = selectedUser;
	}

	public LazyDataModel<User> getLazyUsers() {
		return lazyUsers;
	}
	
	public String editStatus() {
		if(selectedUser != null) {
			System.out.println(selectedUser.getBlocked());
			userDAO.merge(selectedUser);
			System.out.println(selectedUser.getFirstName());
			
		}
		return PAGE_STAY_AT_THE_SAME;
	}
	
	public void clearFilter() {
		user = new User();
		user.setBlocked((byte)2);
		user.setRole("all");
	}

}
