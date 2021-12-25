package jsf.BookStock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import jsf.dao.BookInfoDAO;
import jsf.dao.BookstockDAO;
import jsf.entities.Bookinfo;
import jsf.entities.Bookstock;

@Named
@ViewScoped
public class BookStockAddBB implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String PAGE_CONFIRM = "bookStockConfirm?faces-redirect=true";
	private static final String PAGE_STAY_AT_THE_SAME = null;

	private String titleCode;
	private int codesToGenerate = 1;
	private boolean searched = false;
	private boolean foundTitle = false;

	private List<String> codes = new ArrayList<String>();;

	@EJB
	BookstockDAO bookstockDAO;

	@EJB
	BookInfoDAO bookInfoDAO;

	@Inject
	FacesContext ctx;

	@Inject
	Flash flash;

	public String getTitleCode() {
		return titleCode;
	}

	public void setTitleCode(String titleCode) {
		this.titleCode = titleCode;
	}

	public int getCodesToGenerate() {
		return codesToGenerate;
	}

	public void setCodesToGenerate(int codesToGenerate) {
		this.codesToGenerate = codesToGenerate;
	}

	public boolean isSearched() {
		return searched;
	}

	public void setSearched(boolean searched) {
		this.searched = searched;
	}

	public boolean isFoundTitle() {
		return foundTitle;
	}

	public void setFoundTitle(boolean foundTitle) {
		this.foundTitle = foundTitle;
	}

	public List<String> getCodes() {
		return codes;
	}

	public void setCodes(List<String> codes) {
		this.codes = codes;
	}

	public String generateCodes() {
		Random rand = new Random();
		Boolean codeExists;
		String code = "";
		
		searched = true;
		foundTitle = bookInfoDAO.checkExist(titleCode);
		
		if (foundTitle) {
			codes.clear();
			
			while (codesToGenerate > 0) {
				codeExists = true;

				while (codeExists) {
					code = String.valueOf(rand.nextInt((999999 - 100000) + 1) + 100000);

					codeExists = bookstockDAO.checkExist(code);
				}
				codes.add(code);
				
				codesToGenerate--;
			}
			System.out.println(codes);
		}

		return PAGE_STAY_AT_THE_SAME;
	}
	
	public String undoSearch() {
		searched = false;
		foundTitle = false;
		
		return PAGE_STAY_AT_THE_SAME;
	}
	
	public String saveData() {
		Bookstock book;
		Bookinfo title = new Bookinfo();
		title.setIdTitle(bookInfoDAO.getTitleID(titleCode));
		
		for(int x = codes.size()-1; x >= 0; x--) {
			book = new Bookstock();
			book.setCode(codes.get(x));
			book.setBookinfo(title);
			book.setStatus((byte) 1);
			
			bookstockDAO.merge(book);
		}
		
		return PAGE_CONFIRM;
	}
}
