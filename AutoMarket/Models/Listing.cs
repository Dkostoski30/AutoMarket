using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace AutoMarket.Models
{
    public class Listing
    {
        [Key]
        public int ID { get; set; }
        public Listing() { 
            Car = new Car();
            Images = new List<Image>();
        } 
        public virtual Car Car { get; set; }
        [Required]
        [RegularExpression("\\d{2,10}", ErrorMessage = "Enter a valid price!")]
        public string Price{ get; set;}
        [Required]
        [StringLength(100)]
        public string Title { get; set;}
        [Required]
        [StringLength(300)]
        public string Description { get; set; }
        public ApplicationUser User { get; set; }
        public DateTime? Created { get; set; }
        public string Condition {  get; set; }
        public virtual ICollection<Image> Images { get; set; }

    }
}