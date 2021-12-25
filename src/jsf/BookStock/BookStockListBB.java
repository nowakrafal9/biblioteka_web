package jsf.BookStock;

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

import jsf.dao.BookstockDAO;
import jsf.dao.BorrowedDAO;
import jsf.entities.Bookstock;
import jsf.entities.Borrowed;

@Named
@ViewScoped
public class BookStockListBB implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAGE_STAY_AT_THE_SAME = null;

	private String code;
	private String title;
	private byte status = 0;
	private String orderBy = "code";

	private LazyDataModel<Bookstock> lazyBooks;
	private Bookstock selectedBook;

	@Inject
	ExternalContext externalContext;
	@Inject
	Flash flash;

	@EJB
	BookstockDAO bookstockDAO;
	@EJB
	BorrowedDAO borrowedDAO;

	@PostConstruct
	public void init() {
		lazyBooks = new LazyDataModel<Bookstock>() {
			private static final long serialVersionUID = 1L;

			private List<Bookstock> books;

			Map<String, Object> filterParams = new HashMap<String, Object>();

			@Override
			public Bookstock getRowData(String rowKey) {
				for (Bookstock bookstock : books) {
					if (bookstock.getIdBook() == Integer.parseInt(rowKey)) {
						return bookstock;
					}
				}
				return null;
			}

			@Override
			public String getRowKey(Bookstock bookstock) {
				return String.valueOf(bookstock.getIdBook());
			}

			@Override
			public List<Bookstock> load(int offset, int pageSize, Map<String, SortMeta> sortBy,
					Map<String, FilterMeta> filterBy) {

				filterParams.clear();

				if (code != null && code.length() > 0) {
					filterParams.put("code", code);
				}
				if (title != null && title.length() > 0) {
					filterParams.put("title", title);
				}
				filterParams.put("status", status);
				filterParams.put("orderBy", orderBy);

				System.out.println(filterParams);
				books = bookstockDAO.getLazyList(filterParams, offset, pageSize);

				int rowCount = (int) bookstockDAO.countLazyList(filterParams);
				setRowCount(rowCount);

				return books;
			}
		};
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public LazyDataModel<Bookstock> getLazyBooks() {
		return lazyBooks;
	}

	public Bookstock getSelectedBook() {
		return selectedBook;
	}

	public void setSelectedBook(Bookstock selectedBook) {
		this.selectedBook = selectedBook;
	}

	public void clearFilter() {
		code = null;
		title = null;
		status = 0;
		orderBy = "code";
	}
	
	public List<Bookstock> getFullList() {
		return bookstockDAO.getFullList();
	}

	public void onRowSelect(SelectEvent<Bookstock> event) {
		FacesMessage msg = new FacesMessage("Wybrana książka", String.valueOf(event.getObject().getIdBook()));
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public List<Borrowed> getBorrowInfo(Bookstock book) {
		return borrowedDAO.getBorrowInfo(book);
	}

	public String removeBook(Bookstock book) {
		book.setStatus((byte) 0);
		bookstockDAO.merge(book);

		return PAGE_STAY_AT_THE_SAME;
	}

	public String restoreBook(Bookstock book) {
		book.setStatus((byte) 1);
		bookstockDAO.merge(book);

		return PAGE_STAY_AT_THE_SAME;
	}

}
