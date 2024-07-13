using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace AutoMarket.Models
{
    public class City
    {
        public City () { }
        public City (string Name) {
            this.Name = Name;
        }
        [Key]
        public int Id { get; set; }
        public string Name { get; set; }
    }
}