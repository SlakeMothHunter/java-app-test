package models;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

public class BookUpdateDTO {
	

	    @Size(min = 1, max = 255, message = "Title must not be empty")
	    private String title;

	    @Size(min = 1, max = 255, message = "Author must not be empty")
	    private String author;

	    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
	    private Double price;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getAuthor() {
			return author;
		}

		public void setAuthor(String author) {
			this.author = author;
		}

		public Double getPrice() {
			return price;
		}

		public void setPrice(Double price) {
			this.price = price;
		}

	    


}
