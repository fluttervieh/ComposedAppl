package at.fhv.se.collabnotes.application.dto;

public class NoteTitleDTO {

    private String id;
    private String title;

	public NoteTitleDTO(String id, String title) {
        this.id = id;
        this.title = title;
    }
    
    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
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
        NoteTitleDTO other = (NoteTitleDTO) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        return true;
    }

    // required for JSON serialisation
    @SuppressWarnings("unused")
    private NoteTitleDTO() {}
}
