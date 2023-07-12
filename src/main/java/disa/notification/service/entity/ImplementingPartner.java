package disa.notification.service.entity;

import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;

@Entity
@NamedEntityGraph(name = "graph.ImplementingPartner.orgUnits",
               attributeNodes = @NamedAttributeNode(value = "orgUnits"))
public class ImplementingPartner {

    @Id
    private Integer id;
    private String orgName;
    private String mailList;
    private boolean enabled;

    @OneToMany(cascade = CascadeType.DETACH)
    @JoinColumn(name = "implementingPartnerId")
    private Set<OrgUnit> orgUnits;

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getMailList() {
        return mailList;
    }

    public void setMailList(String mailList) {
        this.mailList = mailList;
    }

    public String[] getMailListItems() {
        return mailList.split(",");
    }

    public Set<String> getOrgUnitCodes() {
        return orgUnits.stream().map(OrgUnit::getCode).collect(Collectors.toSet());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<OrgUnit> getOrgUnits() {
        return orgUnits;
    }

    public void setOrgUnits(Set<OrgUnit> orgUnits) {
        this.orgUnits = orgUnits;
    }
}
