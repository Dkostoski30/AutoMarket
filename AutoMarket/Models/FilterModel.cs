using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace AutoMarket.Models
{
    public class FilterModel
    {
        public string PriceFrom { get; set; }
        public string PriceTo { get; set; }
        public string YearFrom { get; set; }
        public string YearTo { get; set; }
        public string KilometersFrom { get; set; }
        public string KilometersTo { get; set; }
        public string kWFrom { get; set; }
        public string kWTo { get; set; }
        public List<string> FuelTypes { get; set; }
        public List<string> BodyTypes { get; set; }
        public List<string> ConditionTypes { get; set; }
        public List<string> TransmitionTypes { get; set; }

        public FilterModel()
        {
            FuelTypes = new List<string>();
            BodyTypes = new List<string>();
            ConditionTypes = new List<string>();
            TransmitionTypes = new List<string>();
        }
    }
}