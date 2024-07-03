using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace AutoMarket.Models
{
    public class Car
    {
        [Required]
        public string Manufacturer { get; set; }
        [Required]
        public string Model { get; set; }
        [Required]
        [RegularExpression("\\d{1,7}", ErrorMessage = "Kilometers must be a number containing 1 to 7 digits!")]
        public string Kilometers { get; set; }
        [Required]
        [RegularExpression("\\d{4}", ErrorMessage = "Enter a valid year for year of first registration!")]
        [Display(Name = "Year of First Registration")]
        public string Registration_Year { get; set;}
        [Required]
        [Display(Name = "Fuel type")]
        public string Fuel_Type { get; set; }

        [Required]
        [Display(Name = "Body Type")]
        public string Body_Type { get; set; }
        [Required]
        public string Transmition {  get; set; }
        [Required]
        [Display(Name = "Number of doors")]
        [RegularExpression("\\d{1,2}", ErrorMessage = "Enter a valid number!")]
        public string number_doors { get; set; }

        [Required]
        [Display(Name = "Number of seats")]
        [RegularExpression("\\d{1,2}", ErrorMessage = "Enter a valid number!")]
        public string number_seats { get; set; }

        [Required]
        [Display(Name = "Kilowatts")]
        [RegularExpression("\\d{1,2,3}", ErrorMessage = "Enter a valid number!")]
        public string Kilowatts { get; set; }
    }
}