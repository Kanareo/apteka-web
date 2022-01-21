package jsf.Category;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.Flash;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import jsf.dao.CategoryDAO;
import jsf.entities.Category;

@Named
@ViewScoped
public class categoryListBB implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private List<Category> category;

	@Inject
	Flash flash;

	@EJB
	CategoryDAO categoryDAO;
	
	@PostConstruct
	public void init() {
		category = getFullList();		
	}	

	public List<Category> getCategory() {
		return category;
	}

	public void setCategory(List<Category> category) {
		this.category = category;
	}

	public List<Category> getFullList() {
		return categoryDAO.getFullList();
	}
	

}
