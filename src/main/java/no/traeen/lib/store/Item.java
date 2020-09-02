package no.traeen.lib.store;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.Null;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import no.traeen.lib.resource.Image;
import no.traeen.lib.users.User;

/* Represents a sellable/buyable item in the store */
@Entity
@Table(name = "items")
public class Item implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private BigInteger id;

	private float price;

	private String name;

	private String description;

	@OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<Image> image;

	private boolean sold;

	@Temporal(javax.persistence.TemporalType.DATE)
	private Date created;

	@Temporal(javax.persistence.TemporalType.DATE)
	private Date updated;

	@OneToOne
	@JoinColumn(name = "seller", referencedColumnName = "id", nullable = true)
	private User seller;

	@OneToOne
	@JoinColumn(name = "buyer", referencedColumnName = "id", nullable = true)
	private User buyer;

	public Item() {
	}

	public Item(String name, String description, float price, User seller) {
		this.setName(name);
		this.setDescription(description);
		this.setPrice(price);
		this.setSeller(seller);
	}

	@PrePersist
	protected void onCreate() {
		this.created = new Date();
		this.updated = new Date();
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public float getPrice() {
		return this.price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<Image> getImage() {
		return this.image;
	}

	public void setImage(Set<Image> image) {
		this.image = image;
	}

	public boolean isSold() {
		return this.sold;
	}

	public void setSold(boolean sold) {
		this.sold = sold;
	}

	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return this.updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public User getSeller() {
		return this.seller;
	}

	public void setSeller(User seller) {
		this.seller = seller;
	}

	public User getBuyer() {
		return this.buyer;
	}

	public void setBuyer(User buyer) {
		this.buyer = buyer;
	}

}