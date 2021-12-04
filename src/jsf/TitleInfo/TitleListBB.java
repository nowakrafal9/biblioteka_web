package jsf.TitleInfo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.Flash;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import jsf.Author.AuthorListBB;
import jsf.dao.BookInfoDAO;
import jsf.entities.Author;
import jsf.entities.Bookinfo;

@Named
@ViewScoped
public class TitleListBB implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAGE_STAY_AT_THE_SAME = null;
	private static final String PAGE_TITLE_EDIT = "/pages/admin/titleEdit?faces-redirect=true";

	private String code;
	private String name;
	private String surname;
	private String title;
	private String publisher;

	private LazyDataModel<Bookinfo> lazyTitles;
	private Bookinfo selectedBook;

	@Inject
	ExternalContext externalContext;

	@Inject
	Flash flash;

	@EJB
	BookInfoDAO bookInfoDAO;

	@PostConstruct
	public void init() {
		lazyTitles = new LazyDataModel<Bookinfo>() {
			private static final long serialVersionUID = 1L;

			private List<Bookinfo> titles;

			Map<String, Object> filterParams = new HashMap<String, Object>();

			@Override
			public Bookinfo getRowData(String rowKey) {
				for (Bookinfo bookinfo : titles) {
					if (bookinfo.getIdTitle() == Integer.parseInt(rowKey)) {
						return bookinfo;
					}
				}
				return null;
			}

			@Override
			public String getRowKey(Bookinfo bookinfo) {
				return String.valueOf(bookinfo.getIdTitle());
			}

			@Override
			public List<Bookinfo> load(int offset, int pageSize, Map<String, SortMeta> sortBy,
					Map<String, FilterMeta> filterBy) {

				filterParams.clear();

				if (code != null && code.length() > 0) {
					filterParams.put("code", code);
				}
				if (name != null && name.length() > 0) {
					filterParams.put("name", name);
				}
				if (surname != null && surname.length() > 0) {
					filterParams.put("surname", surname);
				}
				if (title != null && title.length() > 0) {
					filterParams.put("title", title);
				}
				if (publisher != null && publisher.length() > 0) {
					filterParams.put("publisher", publisher);
				}

				titles = bookInfoDAO.getLazyList(filterParams, offset, pageSize);

				int rowCount = (int) bookInfoDAO.countLazyList(filterParams);
				setRowCount(rowCount);

				return titles;
			}
		};
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public LazyDataModel<Bookinfo> getLazyTitles() {
		return lazyTitles;
	}

	public Bookinfo getSelectedBook() {
		return selectedBook;
	}

	public void setSelectedBook(Bookinfo selectedBook) {
		this.selectedBook = selectedBook;
	}

	public List<Bookinfo> getFullList() {
		return bookInfoDAO.getFullList();
	}

	public String editTitle(Bookinfo bookinfo) {
		flash.put("title", bookinfo);

		return PAGE_TITLE_EDIT;
	}

	public String newTitle() {
		Bookinfo bookinfo = new Bookinfo();

		flash.put("title", bookinfo);

		return PAGE_TITLE_EDIT;
	}

	public String delTitle(Bookinfo bookinfo) {
		bookInfoDAO.remove(bookinfo);

		return PAGE_STAY_AT_THE_SAME;
	}

}
