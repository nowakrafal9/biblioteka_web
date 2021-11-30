package jsf.Borrower;

import java.io.IOException;
import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import jsf.dao.BorrowerDAO;
import jsf.entities.Borrower;

@Named
@ViewScoped
public class BorrowerEditBB implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAGE_BORROWER_LIST = "borrowerList?faces-redirect=true";
	private static final String PAGE_STAY_AT_THE_SAME = null;

	private Borrower borrower = new Borrower();
	private Borrower loaded = null;

	@EJB
	BorrowerDAO borrowerDAO;

	@Inject
	FacesContext ctx;

	@Inject
	Flash flash;

	public Borrower getBorrower() {
		return borrower;
	}

	public void onLoad() throws IOException {
		loaded = (Borrower) flash.get("borrower");

		if (loaded != null) {
			borrower = loaded;
		} else {
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Błędne użycie systemu", null));
		}
	}
	
	public String saveData() {
		if(loaded == null) {
			return PAGE_STAY_AT_THE_SAME;
		}
		
		try {
			if(borrower.getIdBorrower() == null) {
				borrower.setStatus((byte) 1);
				borrowerDAO.create(borrower);
			} else {
				borrowerDAO.merge(borrower);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wystąpił błąd podczas zapisu", null));
			return PAGE_STAY_AT_THE_SAME;
		}
		
		return PAGE_BORROWER_LIST;
	}
}
