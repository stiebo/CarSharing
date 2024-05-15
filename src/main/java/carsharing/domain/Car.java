package carsharing.domain;

public class Car {
    private int id;
    private String name;
    private int companyId;

    public Car (int id, String name, int company_id) {
        this.id = id;
        this.name = name;
        this.companyId = company_id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCompanyId() {
        return companyId;
    }
}
