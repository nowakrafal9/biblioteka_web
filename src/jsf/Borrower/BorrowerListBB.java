package jsf.Borrower;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;

import jsf.dao.BorrowerDAO;
import jsf.entities.Borrower;

@Named
@RequestScoped
public class BorrowerListBB {
	private static final String PAGE_STAY_AT_THE_SAME = null;
	private static final String PAGE_PERSON_EDIT = "borrowerEdit?faces-redirect=true";

	private String borrowerCode;
	private String name;
	private String surname;
	private String city;
	private byte status = 1;

	@Inject
	ExternalContext externalContext;

	@Inject
	Flash flash;

	@EJB
	BorrowerDAO borrowerDAO;

	public String getBorrowerCode() {
		return borrowerCode;
	}

	public void setBorrowerCode(String borrowerCode) {
		this.borrowerCode = borrowerCode;
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public List<Borrower> getFullList() {
		return borrowerDAO.getFullList();
	}

	public List<Borrower> getList() {
		List<Borrower> list = null;

		Map<String, Object> filterParams = new HashMap<String, Object>();

		if (borrowerCode != null && borrowerCode.length() > 0) {
			filterParams.put("borrowerCode", borrowerCode);
		}
		if (name != null && name.length() > 0) {
			filterParams.put("name", name);
		}
		if (surname != null && surname.length() > 0) {
			filterParams.put("surname", surname);
		}
		if (city != null && city.length() > 0) {
			filterParams.put("city", city);
		}
		filterParams.put("status", status);

		list = borrowerDAO.getList(filterParams);

		return list;
	}

	public String editBorrower(Borrower borrower) {
		flash.put("borrower", borrower);

		return PAGE_PERSON_EDIT;
	}

	public String newBorrower() {
		Borrower borrower = new Borrower();

		flash.put("borrower", borrower);

		return PAGE_PERSON_EDIT;
	}

	public String delBorrower(Borrower borrower) {
		borrowerDAO.remove(borrower);
		return PAGE_STAY_AT_THE_SAME;
	}
}
