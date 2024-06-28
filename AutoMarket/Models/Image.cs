using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Web;

namespace AutoMarket.Models
{
    public class Image
    {
        public Image() { }
        public Image(string name) {
            this.Name = name;
        }
        public Image(string name, string path)
        {
            this.Name = name;
            this.Path = path;
        }
        [Key]
        public int ID { get; set; }
        public string Name { get; set; }
        public string Path { get; set; }
        // Foreign Key
        public int Listing_ID { get; set; }

        // Navigation Property
        [ForeignKey("Listing_ID")]
        public virtual Listing Listing { get; set; }
    }
}