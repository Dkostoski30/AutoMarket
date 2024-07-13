using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace AutoMarket.Models
{
    public class Car_Brand
    {
        public Car_Brand()
        {}
        public Car_Brand(string name) {
            Name = name;
        }
        [Key] public int Id { get; set; }
        public string Name { get; set; }
    }
}