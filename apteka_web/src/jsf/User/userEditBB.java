package jsf.User;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import jsf.dao.UserDAO;
import jsf.entities.User;

@Named
@ViewScoped
public class userEditBB implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAGE_STAY_AT_THE_SAME = null;
	private static final String PAGE_MAIN = "/pages/apteka/main?faces-redirect=true"; // zmiana url

	private User user = new User();
	
	@Inject
	FacesContext ctx;
	@Inject
	Flash flash;

	@EJB
	UserDAO userDAO;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String saveData() {
		try {
			if (user.getIdUser() == null) {
				userDAO.create(user);
			}
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Dodano " + user.getFirstName() + " " + user.getSecondName(), null));
		} catch (Exception e) {
			e.printStackTrace();
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wystąpił błąd podczas zapisu", null));
			return PAGE_STAY_AT_THE_SAME;
		}

		return PAGE_STAY_AT_THE_SAME;
	}

}
