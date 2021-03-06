package jsf.Borrowed;

import java.io.Serializable;
import java.sql.Date;
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
public class BorrowedListBB implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAGE_STAY_AT_THE_SAME = null;

	private Date todayDate = java.sql.Date.valueOf(java.time.LocalDate.now());

	private String bookCode;
	private String borrowerCode;
	private byte status = 1;
	private byte returnStatus = 0;
	private String orderBy = "bookCode";

	private LazyDataModel<Borrowed> lazyBorrows;
	private Borrowed selectedBorrow;

	@Inject
	ExternalContext externalContext;
	@Inject
	Flash flash;

	@EJB
	BorrowedDAO borrowedDAO;
	@EJB
	BookstockDAO bookstockDAO;

	@PostConstruct
	public void init() {
		lazyBorrows = new LazyDataModel<Borrowed>() {
			private static final long serialVersionUID = 1L;

			private List<Borrowed> borrows;

			Map<String, Object> filterParams = new HashMap<String, Object>();

			@Override
			public Borrowed getRowData(String rowKey) {
				for (Borrowed borrowed : borrows) {
					if (borrowed.getIdBorrowed() == Integer.parseInt(rowKey)) {
						return borrowed;
					}
				}
				return null;
			}

			@Override
			public String getRowKey(Borrowed borrowed) {
				return String.valueOf(borrowed.getIdBorrowed());
			}

			@Override
			public List<Borrowed> load(int offset, int pageSize, Map<String, SortMeta> sortBy,
					Map<String, FilterMeta> filterBy) {

				filterParams.clear();

				if (bookCode != null && bookCode.length() > 0) {
					filterParams.put("bookCode", bookCode);
				}
				if (borrowerCode != null && borrowerCode.length() > 0) {
					filterParams.put("borrowerCode", borrowerCode);
				}
				filterParams.put("returnStatus", returnStatus); // After returnDue/before borrowDue
				filterParams.put("status", status); // Borrow status - active/returned
				filterParams.put("orderBy", orderBy);

				borrows = borrowedDAO.getLazyList(filterParams, offset, pageSize);
				int rowCount = (int) borrowedDAO.countLazyList(filterParams);
				setRowCount(rowCount);

				return borrows;
			}
		};
	}

	public String getBookCode() {
		return bookCode;
	}

	public void setBookCode(String bookCode) {
		this.bookCode = bookCode;
	}

	public String getBorrowerCode() {
		return borrowerCode;
	}

	public void setBorrowerCode(String borrowerCode) {
		this.borrowerCode = borrowerCode;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public byte getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(byte returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public Borrowed getSelectedBorrow() {
		return selectedBorrow;
	}

	public void setSelectedBorrow(Borrowed selectedBorrow) {
		this.selectedBorrow = selectedBorrow;
	}

	public LazyDataModel<Borrowed> getLazyBorrows() {
		return lazyBorrows;
	}

	public void onRowSelect(SelectEvent<Borrowed> event) {
		FacesMessage msg = new FacesMessage("Wybrane wypo??yczenie", String.valueOf(event.getObject().getIdBorrowed()));
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void clearFilter() {
		bookCode = null;
		borrowerCode = null;
		status = 1;
		returnStatus = 0;
		orderBy = "bookCode";
	}

	public List<Borrowed> getFullList() {
		return borrowedDAO.getFullList();
	}

	public String formatDate(Date oldDate) {
		return String.format("%1$te-%1$tm-%1$tY", oldDate);
	}

	public boolean compareDate(Date date) {
		if (todayDate.after(date)) {
			return true;
		}

		return false;
	}

	public String returnBook(Borrowed borrowed) {
		Bookstock book = bookstockDAO.find(borrowed.getBookstock().getIdBook());

		book.setStatus((byte) 1);

		borrowed.setStatus((byte) 0);
		borrowed.setReturnDate(todayDate);

		borrowedDAO.merge(borrowed);
		bookstockDAO.merge(book);

		return PAGE_STAY_AT_THE_SAME;
	}
}
