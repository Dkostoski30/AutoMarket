using AutoMarket.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using PagedList;

namespace AutoMarket.Controllers
{
    public class HomeController : Controller
    {
        private ApplicationDbContext db = new ApplicationDbContext();

        [AllowAnonymous]
        public ActionResult Index()
        {
            var listings = db.Listings.OrderByDescending(l => l.Created).ToList();
            ViewBag.FuelTypes = new List<string> { "Petrol", "Diesel", "Electric", "Hybrid", "Hydrogen" };
            ViewBag.BodyTypes = new List<string> { "Sedan", "Wagon", "SUV", "Hatchback", "Coupe", "Convertible", "Minivan" };
            ViewBag.TransmitionTypes = new List<string> { "Manual", "Automatic", "Semi-Automatic", "CVT" };
            ViewBag.ConditionTypes = new List<string> { "New", "Used" };
            ViewBag.Cities = db.Cities.ToList();
            return View(listings);
        }

        [AllowAnonymous]
        public ActionResult About()
        {
            ViewBag.Message = "Your application description page.";

            return View();
        }
        [AllowAnonymous]
        public ActionResult Contact()
        {
            ViewBag.Message = "Your contact page.";

            return View();
        }
        [Authorize(Roles = "Admin,Moderator")]
        public ActionResult Managment()
        {
            return View();
        }
    }
}