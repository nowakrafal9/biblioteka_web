package jsf.Employee;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import jsf.dao.UserDAO;
import jsf.entities.User;

@Named
@ViewScoped
public class EmployeeListBB implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAGE_STAY_AT_THE_SAME = null;
	private static final String PAGE_USER_EDIT = "employeeEdit?faces-redirect=true";

	private String login;
	private byte status = 1;

	private LazyDataModel<User> lazyUsers;
	private User selectedUser;

	@Inject
	ExternalContext externalContext;

	@Inject
	Flash flash;

	@EJB
	UserDAO userDAO;

	@PostConstruct
	public void init() {
		lazyUsers = new LazyDataModel<User>() {
			private static final long serialVersionUID = 1L;

			private List<User> users;
			Map<String, Object> filterParams = new HashMap<String, Object>();

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

				filterParams.clear();

				if (login != null && login.length() > 0) {
					filterParams.put("login", login);
				}
				filterParams.put("status", status);
				
				users = userDAO.getLazyList(filterParams, offset, pageSize);

				int rowCount = (int) userDAO.countLazyList(filterParams);
				setRowCount(rowCount);

				return users;
			}
		};
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public LazyDataModel<User> getLazyUsers() {
		return lazyUsers;
	}

	public User getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(User selectedUser) {
		this.selectedUser = selectedUser;
	}

	public void onRowSelect(SelectEvent<User> event) {
		FacesMessage msg = new FacesMessage("Wybrany pracownik", String.valueOf(event.getObject().getIdUser()));
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public List<User> getFullList() {
		return userDAO.getFullList();
	}

	public String editUser(User user) {
		flash.put("user", user);

		return PAGE_USER_EDIT;
	}

	public String newUser() {
		User user = new User();

		flash.put("user", user);

		return PAGE_USER_EDIT;
	}

	public String blockUser(User user) {
		user.setStatus((byte) 0);
		userDAO.merge(user);

		return PAGE_STAY_AT_THE_SAME;
	}

	public String unblockUser(User user) {
		user.setStatus((byte) 1);
		userDAO.merge(user);

		return PAGE_STAY_AT_THE_SAME;
	}
}
