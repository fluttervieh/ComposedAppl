package at.fhv.se.collabnotes.domain.model;

public class NoteId {
    private String id;

	public NoteId(String id) {
		this();
        this.setId(id);
    }

    public String id() {
        return this.id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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

		NoteId other = (NoteId) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
			
		return true;
	}

	// needed by Hibernate and JSON - but can keep it private
    private NoteId() {
    }

	private void setId(String id) {
		this.id = id;
	}
}
