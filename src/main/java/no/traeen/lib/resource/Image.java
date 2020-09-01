package no.traeen.lib.resource;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

	private String mimeType;

	private String size;

	private Item owner;

}