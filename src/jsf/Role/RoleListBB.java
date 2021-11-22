package jsf.Role;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.Flash;

import jsf.dao.RoleDAO;
import jsf.entities.Role;

@Named
@RequestScoped
public class RoleListBB {
	private static final String PAGE_STAY_AT_THE_SAME = null;

	@Inject
	ExternalContext extcontext;

	@Inject
	Flash flash;
	
	@EJB
	RoleDAO roleDAO;
	
	public List<Role> getFullList(){
		return roleDAO.getFullList();
	}
}