package no.traeen.lib.resource;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import no.traeen.lib.store.Item;

/**
 * Represents a storeable image.
 */
@Entity
@Table(name = "images")
public class Image implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private BigInteger id;

	private String name;

	@Column(name = "mime_type")
	private String mimeType;

	private float size;

	@JoinColumn(name = "owner", referencedColumnName = "id", nullable = true)
	@ManyToOne
	private Item owner;

	public Image() {

	}

	public BigInteger getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMimeType() {
		return this.mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public float getSize() {
		return this.size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public Item getOwner() {
		return this.owner;
	}

	public void setOwner(Item owner) {
		this.owner = owner;
	}

}