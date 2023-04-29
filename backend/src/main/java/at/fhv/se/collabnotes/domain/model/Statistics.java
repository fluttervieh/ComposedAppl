package at.fhv.se.collabnotes.domain.model;

public class Statistics {
	@SuppressWarnings("unused")
	private Long id;
	@SuppressWarnings("unused")
	private int statsId;
    private int items;
    private int notes;

	public static Statistics create(int notes, int items) {
		if (notes == 0 && items != 0) {
			throw new IllegalStateException("If 0 notes, then items also have to be 0");
		}

		return new Statistics(notes, items);
	}

	public static Statistics empty() {
		return Statistics.create(0, 0);
	}

	public int itemCount() {
		return this.items;
	}

	public int noteCount() {
		return this.notes;
	}

	public void incrementItems() {
        this.items++;
	}

	public void incrementNotes() {
		this.notes++;
	}

	public void decrementItems() {
		if (this.items <= 0) {
			throw new IllegalStateException("Cannot decrement items below 0");
		}

		this.items--;
	}

	public void decrementNotes(int itemsOfNote) {
		if (this.notes <= 0) {
			throw new IllegalStateException("Cannot decrement notes below 0");
		}

		if (this.items - itemsOfNote < 0) {
			throw new IllegalStateException("Cannot decrement items below 0");
		}

		this.items -= itemsOfNote;
		this.notes--;

		if (this.notes == 0 && this.items != 0) {
			throw new IllegalStateException("If 0 notes, then items also have to be 0");
		}
	}

	public double averageItems() {
		if (0 == this.notes) {
			return 0;
		}

		return (double) this.items / (double) this.notes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + items;
		result = prime * result + notes;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Statistics other = (Statistics) obj;
		if (items != other.items)
			return false;
		if (notes != other.notes)
			return false;
		return true;
	}

	private Statistics(int notes, int items) {
        this.notes = notes;
        this.items = items;
	}

	private Statistics() {
	}
}
