package jsf.Genre;

import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;

import jsf.dao.GenreDAO;
import jsf.entities.Genre;

@Named
@RequestScoped
public class GenreListBB {
	private static final String PAGE_STAY_AT_THE_SAME = null;
	
	@Inject
	ExternalContext externalContext;
	
	@Inject
	Flash flash;
	
	@EJB
	GenreDAO genreDAO;
	
	public List<Genre> getFullList(){
		return genreDAO.getFullList();
	}
}
