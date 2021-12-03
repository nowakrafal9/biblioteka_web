package jsf.TitleInfo;

import java.io.IOException;
import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import jsf.dao.BookInfoDAO;
import jsf.entities.Bookinfo;

@Named
@ViewScoped
public class TitleEditBB implements Serializable{
	private static final long serialVersionUID = 1L;

	private static final String PAGE_TITLE_LIST = "/pages/library/titleList?faces-redirect=true";
	private static final String PAGE_STAY_AT_THE_SAME = null;

	private Bookinfo bookinfo = new Bookinfo();
	private Bookinfo loaded = null;
	
	@EJB
	BookInfoDAO bookInfoDAO;

	@Inject
	FacesContext ctx;

	@Inject
	Flash flash;
	
	public Bookinfo getBookinfo() {
		return bookinfo;
	}

	public void onLoad() throws IOException {
		loaded = (Bookinfo) flash.get("title");

		if (loaded != null) {
			bookinfo = loaded;
		} else {
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Błędne użycie systemu", null));
		}
	}
	
	public String saveData() {
		if (loaded == null) {
			return PAGE_STAY_AT_THE_SAME;
		}

		try {
			if (bookinfo.getIdTitle() == null) {
				bookInfoDAO.create(bookinfo);
			} else {
				bookInfoDAO.merge(bookinfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wystąpił błąd podczas zapisu", null));
			return PAGE_STAY_AT_THE_SAME;
		}

		return PAGE_TITLE_LIST;
	}
}
