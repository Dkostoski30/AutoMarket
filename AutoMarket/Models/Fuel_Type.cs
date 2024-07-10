using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace AutoMarket.Models
{
    public class Fuel_Type
    {
        [Key]
        public int ID { get; set; }
        public string Name { get; set; }
    }
}