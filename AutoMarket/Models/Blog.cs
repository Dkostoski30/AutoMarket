using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace AutoMarket.Models
{
    public class Blog
    {
            [Key]        
            public int ID { get; set; }
            [Required(ErrorMessage = "Title is required")]
            [StringLength(100, ErrorMessage = "Title cannot be longer than 100 characters")]
            public string Title { get; set; }
            [Required(ErrorMessage = "Content is required")]
            public string Content { get; set; }
 
            public string ImageUrl { get; set; }
            public DateTime Date { get; set; }
            
    }
}