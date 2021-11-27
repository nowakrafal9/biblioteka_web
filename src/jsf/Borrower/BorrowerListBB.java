package jsf.Borrower;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private String name;
	private String surname;

	@Inject
	ExternalContext externalContext;

	@Inject
	Flash flash;

	@EJB
	BorrowerDAO borrowerDAO;

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

	public List<Borrower> getFullList() {
		return borrowerDAO.getFullList();
	}

	public List<Borrower> getList() {
		List<Borrower> list = null;

		Map<String, Object> filterParams = new HashMap<String, Object>();

		if (name != null && name.length() > 0) {
			filterParams.put("name", name);
		}
		if (surname != null && surname.length() > 0) {
			filterParams.put("surname", surname);
		}

		System.out.println(filterParams.get("name"));
		System.out.println(filterParams.get("surname"));

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
