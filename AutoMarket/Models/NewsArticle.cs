using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace AutoMarket.Models
{
    public class NewsArticle
    {
        public string Title { get; set; }
        public string Description { get; set; }
        public string Url { get; set; }
        public string ImageUrl { get; set; }
        public DateTime PublishedAt { get; set; }
    }
}