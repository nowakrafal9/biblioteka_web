package jsf.Employee;

import java.io.IOException;
import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import jsf.dao.RoleDAO;
import jsf.dao.UserDAO;
import jsf.entities.Role;
import jsf.entities.User;

@Named
@ViewScoped
public class EmployeeEditBB implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAGE_USER_LIST = "employeeList?faces-redirect=true";
	private static final String PAGE_STAY_AT_THE_SAME = null;

	private User user = new User();
	private User loaded = null;

	private String newPass;
	
	@Inject
	FacesContext ctx;

	@Inject
	Flash flash;

	@EJB
	UserDAO userDAO;
	
	@EJB
	RoleDAO roleDAO;
	
	public User getUser() {
		return user;
	}

	public String getNewPass() {
		return newPass;
	}

	public void setNewPass(String newPass) {
		this.newPass = newPass;
	}

	public void onLoad() throws IOException {
		loaded = (User) flash.get("user");

		if (loaded != null) {
			user = loaded;
		} else {
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Błędne użycie systemu", null));
		}
	}

	public String saveData() {
		if (loaded == null) {
			return PAGE_STAY_AT_THE_SAME;
		}

		if (newPass != null) {
			user.setPassword((String) newPass);
		}
		
		try {
			if (user.getIdUser() == null) {
				Role role = new Role();
				role.setIdRole(2);
				user.setRole(role);
				
				userDAO.create(user);
			} else {
				userDAO.merge(user);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wystąpił błąd podczas zapisu", null));
			return PAGE_STAY_AT_THE_SAME;
		}

		return PAGE_USER_LIST;
	}
}
