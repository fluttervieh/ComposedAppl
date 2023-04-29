package at.fhv.se.collabnotes.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class StatisticsDTO {
    @Schema(required = true)
    private int items;
    @Schema(required = true)
    private int notes;
    @Schema(required = true)
    private double averageItems;

	public int getItems() {
		return this.items;
	}

	public int getNotes() {
		return this.notes;
	}

	public double getAverageItems() {
		return averageItems;
	}

    private StatisticsDTO() {
    }
    
    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private StatisticsDTO instance;

        private Builder() {
            this.instance = new StatisticsDTO();
        }

        public Builder setNoteCount(int noteCount) {
            this.instance.notes = noteCount;
            return this;
        }

        public Builder setItemCount(int itemCount) {
            this.instance.items = itemCount;
            return this;
        }

        public Builder setAverageItems(double avg) {
            this.instance.averageItems = avg;
            return this;
        }

        public StatisticsDTO build() {
            return this.instance;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(averageItems);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        StatisticsDTO other = (StatisticsDTO) obj;
        if (Double.doubleToLongBits(averageItems) != Double.doubleToLongBits(other.averageItems))
            return false;
        if (items != other.items)
            return false;
        if (notes != other.notes)
            return false;
        return true;
    }
}
