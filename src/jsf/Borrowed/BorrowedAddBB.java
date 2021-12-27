package jsf.Borrowed;

import java.io.Serializable;
import java.sql.Date;

import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import jsf.dao.BookInfoDAO;
import jsf.dao.BookstockDAO;
import jsf.dao.BorrowedDAO;
import jsf.dao.BorrowerDAO;
import jsf.dao.UserDAO;
import jsf.entities.Bookinfo;
import jsf.entities.Bookstock;
import jsf.entities.Borrowed;
import jsf.entities.Borrower;
import jsf.entities.User;

@Named
@ViewScoped
public class BorrowedAddBB implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String PAGE_STAY_AT_THE_SAME = null;
	private static final String PAGE_BORROWED_LIST = "borrowedList?faces-redirect=true";

	private String bookCode;
	private String borrowerCode;
	private String login;
	private int borrowTime = 0;
	private boolean searched = false;
	private boolean foundBook = false;
	private boolean foundBorrower = false;

	private Borrower borrower;
	private Bookinfo bookinfo;

	@EJB
	BookstockDAO bookstockDAO;
	@EJB
	BorrowerDAO borrowerDAO;
	@EJB
	BorrowedDAO borrowedDAO;
	@EJB
	BookInfoDAO bookInfoDAO;
	@EJB
	UserDAO userDAO;

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

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public int getBorrowTime() {
		return borrowTime;
	}

	public void setBorrowTime(int borrowTime) {
		this.borrowTime = borrowTime;
	}

	public boolean isSearched() {
		return searched;
	}

	public boolean isFoundBook() {
		return foundBook;
	}

	public boolean isFoundBorrower() {
		return foundBorrower;
	}

	public Borrower getBorrower() {
		return borrower;
	}

	public void setBorrower(Borrower borrower) {
		this.borrower = borrower;
	}

	public Bookinfo getBookinfo() {
		return bookinfo;
	}

	public void setBookinfo(Bookinfo bookinfo) {
		this.bookinfo = bookinfo;
	}

	public String findData() {
		searched = true;
		foundBook = bookstockDAO.checkBorrowed(bookCode);
		foundBorrower = borrowerDAO.checkActive(borrowerCode);

		if (foundBook) {
			Bookstock book = bookstockDAO.find(bookstockDAO.getBookID(bookCode));
			this.bookinfo = bookInfoDAO.find(book.getBookinfo().getIdTitle());
		}
		if (foundBorrower) {
			this.borrower = borrowerDAO.find(borrowerDAO.getBorrowerID(borrowerCode));
		}

		return PAGE_STAY_AT_THE_SAME;
	}

	public String undoSearch() {
		searched = false;
		foundBook = false;
		foundBorrower = false;

		return PAGE_STAY_AT_THE_SAME;
	}

	public String borrowBook() {
		Bookstock book = bookstockDAO.find(bookstockDAO.getBookID(bookCode));
		Borrower borrower = borrowerDAO.find(borrowerDAO.getBorrowerID(borrowerCode));
		User user = userDAO.find(userDAO.getUserID(login));
		Borrowed borrowedBook = new Borrowed();

		Date today = java.sql.Date.valueOf(java.time.LocalDate.now());
		Date returnDate = null;

		if (borrowTime == 0) {
			returnDate = java.sql.Date.valueOf(java.time.LocalDate.now().plusWeeks(2));
		} else if (borrowTime == 1) {
			returnDate = java.sql.Date.valueOf(java.time.LocalDate.now().plusMonths(1));
		} else if (borrowTime == 2) {
			returnDate = java.sql.Date.valueOf(java.time.LocalDate.now().plusMonths(2));
		}

		borrowedBook.setBookstock(book);
		borrowedBook.setBorrower(borrower);
		borrowedBook.setUser(user);
		borrowedBook.setBorrowDate(today);
		borrowedBook.setReturnDue(returnDate);
		borrowedBook.setReturnDate(null);
		borrowedBook.setStatus((byte) 1);

		book.setStatus((byte) 2);

		borrowedDAO.create(borrowedBook);
		bookstockDAO.merge(book);

		return PAGE_BORROWED_LIST;
	}
}
