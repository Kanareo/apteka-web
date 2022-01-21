package jsf.Brand;

import java.io.Serializable;
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

import jsf.dao.BrandDAO;
import jsf.entities.Brand;

@Named
@ViewScoped
public class brandListBB implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private LazyDataModel<Brand> lazyBrands;
	private Brand selectedBrand;
	
	private List<Brand> brands;

	@Inject
	Flash flash;

	@EJB
	BrandDAO brandDAO;
	
	@PostConstruct
	public void init() {
		brands = getFullList();
		
		lazyBrands = new LazyDataModel<Brand>() {
			private static final long serialVersionUID = 1L;

			private List<Brand> brands;		

			@Override
			public Brand getRowData(String rowKey) {
				for (Brand brand : brands) {
					if (brand.getIdBrand() == Integer.parseInt(rowKey)) {
						return brand;
					}
				}
				return null;
			}

			@Override
			public String getRowKey(Brand brand) {
				return String.valueOf(brand.getIdBrand());
			}

			@Override
			public List<Brand> load(int offset, int pageSize, Map<String, SortMeta> sortBy,
					Map<String, FilterMeta> filterBy) {
				
				brands = brandDAO.getLazyList(null, offset, pageSize);
				int rowCount = (int) brandDAO.countLazyList(null);
				setRowCount(rowCount);

				return brands;
			}
		};
	}	

	public List<Brand> getBrands() {
		return brands;
	}


	public LazyDataModel<Brand> getLazyBrands() {
		return lazyBrands;
	}

	public void setLazyBrands(LazyDataModel<Brand> lazyBrands) {
		this.lazyBrands = lazyBrands;
	}

	public Brand getSelectedBrand() {
		return selectedBrand;
	}

	public void setSelectedBrand(Brand selectedBrand) {
		this.selectedBrand = selectedBrand;
	}

	public void onRowSelect(SelectEvent<Brand> event) {
		FacesMessage msg = new FacesMessage("Wybrana marka", String.valueOf(event.getObject().getIdBrand()));
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public List<Brand> getFullList() {
		return brandDAO.getFullList();
	}
	

}
