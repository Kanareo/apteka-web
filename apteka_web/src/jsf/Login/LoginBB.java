package jsf.Login;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.simplesecurity.RemoteClient;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jsf.dao.UserDAO;
import jsf.entities.User;

@Named
@RequestScoped
public class LoginBB {
	@Inject
	UserDAO userDAO;
	
	private static final String PAGE_STAY_AT_THE_SAME = null;
	private static final String PAGE_LOGIN = "/pages/login";
	private static final String PAGE_MAIN = "/pages/apteka/main?faces-redirect=true"; //zmiana URL
	
	private String email;
	private String password;
			
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String doLogin() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		
		User user = userDAO.getUser(email);
		
		if(user == null || !user.getPassword().equals(password) || user.getBlocked() == 1) {
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
				"Niepoprawny login lub hasło", null));
			return PAGE_STAY_AT_THE_SAME;
		}
		
		RemoteClient<User> client = new RemoteClient<User>();
		client.setDetails(user);
		client.getRoles().add(user.getRole());
		
		HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
		client.store(request);		
		
		return PAGE_MAIN;
	}
	
	public String doLogout() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
			.getExternalContext().getSession(true);
		
		session.invalidate();
		
		return PAGE_LOGIN;
	}
}