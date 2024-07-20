using AutoMarket.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using PagedList;
using Newtonsoft.Json;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web.UI;

namespace AutoMarket.Controllers
{
/*    public class NewsService
    {
        private readonly string _apiKey = "b540f019d6be48fb9e9d1aaf75668678";
        private readonly string _baseUrl = "https://newsapi.org/v2/everything";

        public async Task<List<NewsArticle>> GetCarNewsAsync()
        {
            using (HttpClient client = new HttpClient())
            {
                string url = $"{_baseUrl}?q=cars&apiKey={_apiKey}";
                var response = await client.GetStringAsync(url);
                var result = JsonConvert.DeserializeObject<NewsApiResponse>(response);
                return result.Articles;
            }
        }
    }
*/

    public class NewsApiResponse
    {
        public List<NewsArticle> Articles { get; set; }
    }

    public class HomeController : Controller
    {
        /*private readonly NewsService _newsService = new NewsService();*/
        private ApplicationDbContext db = new ApplicationDbContext();

        [AllowAnonymous]
        public ActionResult Index(int? page)
        {
            int pageSize = 3; // Number of blogs per page
            int pageNumber = (page ?? 1); // Default to the first page if no page number is provided

            var listings = db.Listings.Where(l => l.Approved == true).OrderByDescending(l => l.Created).ToList();
            ViewBag.FuelTypes = db.Fuel_Types.Select(ft => ft.Name).ToList();
            ViewBag.BodyTypes = db.Body_Types.Select(bt => bt.Name).ToList();
            ViewBag.TransmitionTypes = db.Transmission_Types.Select(tt => tt.Name).ToList();
            ViewBag.ConditionTypes = db.Condition_Types.Select(ct => ct.Name).ToList();
            ViewBag.Cities = db.Cities.ToList();
            ViewBag.Blogs = db.Blogs.OrderByDescending(b => b.Date).ToPagedList(pageNumber, pageSize);
            return View(listings);
        }

        [AllowAnonymous]
        public ActionResult About()
        {
            ViewBag.Message = "Your application description page.";

            return View();
        }
        [AllowAnonymous]
        public ActionResult FAQ()
        {
            return View();
        }
        [AllowAnonymous]
        public ActionResult Contact()
        {
            ViewBag.Message = "Your contact page.";

            return View();
        }
        [Authorize(Roles = "Admin,Moderator")]
        public ActionResult Management()
        {
            ViewBag.PendingListings = db.Listings.Where(l => l.Approved == false).Count();
            ViewBag.
            Listings = db.Listings.Where(l => l.Approved).Count();
            ViewBag.Users = db.Users.Count();
            ViewBag.CarBrands = db.Car_Brands.Count();
            ViewBag.Conditions = db.Condition_Types.Count();
            ViewBag.Transmissions = db.Transmission_Types.Count();
            ViewBag.Cities = db.Cities.Count();

            return View();
        }
    }
}