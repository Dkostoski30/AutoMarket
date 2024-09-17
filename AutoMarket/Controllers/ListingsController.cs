using System;
using System.Collections;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Drawing.Printing;
using System.IO;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.Configuration;
using System.Web.Mvc;
using System.Web.UI;
using AutoMarket.Models;
using Microsoft.Ajax.Utilities;
using Microsoft.AspNet.Identity;
using PagedList;

namespace AutoMarket.Controllers
{
    public class NumericStringComparer : IComparer<string>
    {
        private readonly Func<string, string, int> _compareFunction;

        public NumericStringComparer(Func<string, string, int> compareFunction)
        {
            _compareFunction = compareFunction;
        }

        public int Compare(string x, string y)
        {
            return _compareFunction(x, y);
        }
    }
    public class ListingsController : Controller
    {
        private ApplicationDbContext db = new ApplicationDbContext();
        
        // GET: Listings
        [AllowAnonymous]
        public ActionResult Index(int? page)
        {
            
            int pageSize = 8; // Number of items per page
            int pageNumber = (page ?? 1);
            List<string> fuel_types = db.Fuel_Types.Select(ft => ft.Name).ToList();
            List<string> body_types = db.Body_Types.Select(bt =>  bt.Name).ToList();
            List<string> condition_types = db.Condition_Types.Select(ct => ct.Name).ToList();
            List<string> transmission_types = db.Transmission_Types.Select(tt => tt.Name).ToList();
            ViewBag.FuelTypes = fuel_types;
            ViewBag.BodyTypes = body_types;
            ViewBag.TransmitionTypes = transmission_types;
            ViewBag.ConditionTypes = condition_types;
            
            var listingsPaged = db.Listings.Include(l => l.User).Where(l => l.Approved == true).OrderByDescending(l => l.Created).ToPagedList(pageNumber, pageSize);
            return View(listingsPaged);
        }
        [Authorize(Roles = "Moderator,Admin")]
        public ActionResult ReviewListings(int? page)
        {

            int pageSize = 8; // Number of items per page
            int pageNumber = (page ?? 1);
            List<string> fuel_types = db.Fuel_Types.Select(ft => ft.Name).ToList();
            List<string> body_types = db.Body_Types.Select(bt => bt.Name).ToList();
            List<string> condition_types = db.Condition_Types.Select(ct => ct.Name).ToList();
            List<string> transmission_types = db.Transmission_Types.Select(tt => tt.Name).ToList();
            ViewBag.FuelTypes = fuel_types;
            ViewBag.BodyTypes = body_types;
            ViewBag.TransmitionTypes = transmission_types;
            ViewBag.ConditionTypes = condition_types;

            var listingsPaged = db.Listings.Include(l => l.User).Where(l => l.Approved == false).OrderByDescending(l => l.Created).ToPagedList(pageNumber, pageSize);
            return View(listingsPaged);
        }
        /*   public ActionResult Index(ICollection<Listing> listings)
           {
               ViewBag.FuelTypes =  db.Fuel_Types.Select(ft => ft.Name).ToList();
               ViewBag.BodyTypes = new List<string> { "Sedan", "SUV", "Hatchback", "Coupe", "Convertible", "Minivan" };
               ViewBag.TransmitionTypes = db.Transmission_Types.Select(tt => tt.Name).ToList();
               ViewBag.ConditionTypes = db.Condition_Types.Select(ct => ct.Name).ToList();

               return View(listings);
           }*/
        [AllowAnonymous]
        public ActionResult FilterBodyType(int id)
        {
            int pageSize = 8; // Number of items per page
            int pageNumber = 1;
            ViewBag.FuelTypes = db.Fuel_Types.Select(ft => ft.Name).ToList();
            ViewBag.BodyTypes = db.Body_Types.Select(bt => bt.Name).ToList();
            ViewBag.TransmitionTypes = db.Transmission_Types.Select(tt => tt.Name).ToList();
            ViewBag.ConditionTypes = db.Condition_Types.Select(ct => ct.Name).ToList();
            if (id == 1)
            {
                var listings = db.Listings.Where(l => l.Approved == true).Include(l => l.User).Where(l => l.Car.Body_Type == "SUV").Where(l => l.Approved).OrderByDescending(l => l.Created).ToPagedList(pageNumber, pageSize);
                return View("Index", listings);
            }
            else if (id == 2)
            {
                var listings = db.Listings.Where(l => l.Approved == true).Include(l => l.User).Where(l => l.Car.Body_Type == "Sedan").Where(l => l.Approved).OrderByDescending(l => l.Created).ToPagedList(pageNumber, pageSize);
                return View("Index", listings);

            }
            else if(id == 3)
            {
                var listings = db.Listings.Where(l => l.Approved == true).Include(l => l.User).Where(l => l.Car.Body_Type == "Hatchback").Where(l => l.Approved).OrderByDescending(l => l.Created).ToPagedList(pageNumber, pageSize);
                return View("Index", listings);

            }else if(id == 4)
            {
                var listings = db.Listings.Where(l => l.Approved == true).Include(l => l.User).Where(l => l.Car.Body_Type == "Coupe").Where(l => l.Approved).OrderByDescending(l => l.Created).ToPagedList(pageNumber, pageSize);
                return View("Index", listings);

            }
            else
            {
                var listings = db.Listings.Where(l => l.Approved == true).Include(l => l.User).Where(l => l.Approved).OrderByDescending(l => l.Created).ToPagedList(pageNumber, pageSize);
                return View("Index", listings);

            }

            
        }
        [AllowAnonymous]
        [HttpGet]
        public ActionResult BrowseByCities(CitySelectionViewModel model, int? page)
        {
            ViewBag.FuelTypes = db.Fuel_Types.Select(ft => ft.Name).ToList();
            ViewBag.BodyTypes = db.Body_Types.Select(bt => bt.Name).ToList();
            ViewBag.TransmitionTypes = db.Transmission_Types.Select(tt => tt.Name).ToList();
            ViewBag.ConditionTypes = db.Condition_Types.Select(ct => ct.Name).ToList();
            if (model.SelectedCities != null && model.SelectedCities.Any())
            {
                List<string> cities = db.Cities.Where(c => model.SelectedCities.Contains(c.Id)).Select(c => c.Name).ToList();
                var listings = db.Listings.Where(l => l.Approved == true).Include(l => l.User).Where(l => cities.Contains(l.User.City)).Where(l => l.Approved).OrderByDescending(l => l.Created).ToPagedList(1, 8);
                return View("Index", listings);
            }
            else
            {
                return RedirectToAction("Index", "Home");
            }
        }
        // GET: Listings/Details/5
        [AllowAnonymous]
        public ActionResult Details(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            var listings = db.Listings.Include(l => l.User).ToList();
            Listing listing = db.Listings.Find(id);
            if (listing == null)
            {
                return HttpNotFound();
            }
            return View(listing);
        }

        // GET: Listings/Create
      
        public ActionResult Create()
        {
            List<string> fuel_types = db.Fuel_Types.Select(ft => ft.Name).ToList();
            List<string> body_types = db.Body_Types.Select(bt => bt.Name).ToList();
            List<string> condition_types = db.Condition_Types.Select(ct => ct.Name).ToList();
            List<string> transmission_types = db.Transmission_Types.Select(tt => tt.Name).ToList();
            List<string> car_brands = db.Car_Brands.Select(cb => cb.Name).ToList();

            ViewBag.FuelTypes = new SelectList(fuel_types);
            ViewBag.CarBrands = new SelectList(car_brands);
            ViewBag.TransmissionTypes = new SelectList(transmission_types);
            ViewBag.BodyTypes = new SelectList(body_types);
            ViewBag.ConditionTypes = condition_types;

            return View();
        }

        // POST: Listings/Create
        
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Create([Bind(Include = "ID,Car,Price,Title,Description,Created,Condition")] Listing listing)
        {
            if (ModelState.IsValid)
            {
                listing.Created = DateTime.Now;
                listing.Approved = false;

                var files = Request.Files;
                for (int i = 0; i < files.Count; i++)
                {
                    var file = files[i];
                    if (file != null && file.ContentLength > 0)
                    {
                        string filename = Path.GetFileName(file.FileName);
                        string path = Path.Combine(Server.MapPath("~/Content/Images/car_images/"), filename);
                        Image image = new Image(filename, path);
                        file.SaveAs(path);
                        listing.Images.Add(image);
                    }
                }
                var user_id = User.Identity.GetUserId();
                var user = db.Users.SingleOrDefault(u => u.Id == user_id);
                if (user != null)
                {
                    
                    listing.User = user;
                }
                db.Listings.Add(listing);
                db.SaveChanges();
                return RedirectToAction("Index");
            }

            return View(listing);
        }

        // GET: Listings/Edit/5
        public ActionResult Edit(int? id)
        {
            List<string> fuel_types = db.Fuel_Types.Select(ft => ft.Name).ToList();
            List<string> body_types = db.Body_Types.Select(bt => bt.Name).ToList();
            List<string> condition_types = db.Condition_Types.Select(ct => ct.Name).ToList();
            List<string> transmission_types = db.Transmission_Types.Select(tt => tt.Name).ToList();
            List<string> car_brands = db.Car_Brands.Select(cb => cb.Name).ToList();

            ViewBag.FuelTypes = new SelectList(fuel_types);
            ViewBag.CarBrands = new SelectList(car_brands);
            ViewBag.TransmissionTypes = new SelectList(transmission_types);
            ViewBag.BodyTypes = new SelectList(body_types);
            ViewBag.ConditionTypes = condition_types;

            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Listing listing = db.Listings.Include(l => l.User).ToList().Find(l => l.ID == id);
            if (listing == null)
            {
                return HttpNotFound();
            }
            var currentUser = User.Identity.Name;
            if (User.IsInRole("Admin") || listing.User.UserName == currentUser)
            {
                return View(listing);
            }
            else
            {
                return RedirectToAction("Details", new {id = id});
            }
        }

        // POST: Listings/Edit/5
        // To protect from overposting attacks, enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Edit([Bind(Include = "ID,Car,Price,Title,Description,Created,Condition")] Listing listing)
        {
            if (ModelState.IsValid)
            {
                db.Entry(listing).State = EntityState.Modified;
                var files = Request.Files;
                if(files.Count != 0)
                {
                    var images = db.Images.Where(i => i.Listing_ID == listing.ID).ToList();
                    foreach (var image in images)
                    {
                        string filePath = image.Path;
                        if (System.IO.File.Exists(filePath))
                        {
                            System.IO.File.Delete(filePath);
                        }
                        db.Images.Remove(image);

                    }
                }
                for (int i = 0; i < files.Count; i++)
                {
                    var file = files[i];
                    if (file != null && file.ContentLength > 0)
                    {
                        string filename = Path.GetFileName(file.FileName);
                        string path = Path.Combine(Server.MapPath("~/Content/Images/car_images/"), filename);
                        Image image = new Image(filename, path);
                        file.SaveAs(path);
                        listing.Images.Add(image);
                    }
                }
                
                db.SaveChanges();
                return RedirectToAction("Index");
            }
            return View(listing);
        }

        // GET: Listings/Delete/5
        /*public ActionResult Delete(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Listing listing = db.Listings.Find(id);
            if (listing == null)
            {
                return HttpNotFound();
            }
            return View(listing);
        }
*/
        // POST: Listings/Delete/5
  /*      [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
  */      
        public ActionResult Delete(int id)
        {
            Listing listing = db.Listings.Find(id);
            var images = db.Images.Where(i => i.Listing_ID == id).ToList();
            foreach (var image in images)
            {
                string filePath = image.Path;
                if (System.IO.File.Exists(filePath))
                {
                    System.IO.File.Delete(filePath);
                }
                db.Images.Remove(image);
               
            }
            db.Listings.Remove(listing);
            db.SaveChanges();
            return RedirectToAction("Index");
        }
        [Authorize(Roles = "Moderator,Admin")]
        public ActionResult Approve(int id)
        {
            Listing listing = db.Listings.Find(id);
            listing.Approved = true;
            db.SaveChanges();
            return RedirectToAction("ReviewListings");
        }
        private int CompareNumericStrings(string numStr1, string numStr2)
        {
          
            numStr1 = numStr1.TrimStart('0');
            numStr2 = numStr2.TrimStart('0');

           
            if (numStr1.Length > numStr2.Length)
                return 1;
            if (numStr1.Length < numStr2.Length)
                return -1; 

            for (int i = 0; i < numStr1.Length; i++)
            {
                if (numStr1[i] > numStr2[i])
                    return 1; 
                if (numStr1[i] < numStr2[i])
                    return -1; 
            }

            
            return 0;
        }
        [Authorize(Roles="Admin")]
        public ActionResult ManageListings()
        {
            var listings = db.Listings.Include(l => l.User).ToList();
            return View(listings);
        }

        [AllowAnonymous]
        [HttpGet]
        public ActionResult ApplyFilters(int? page)
        {

            ViewBag.Title = "ApplyFilters";
          


            int pageSize = 8;
            int pageNumber = (page ?? 1);
            ViewBag.FuelTypes =  db.Fuel_Types.Select(ft => ft.Name).ToList();
            ViewBag.BodyTypes = db.Body_Types.Select(bt => bt.Name).ToList();
            ViewBag.TransmitionTypes = db.Transmission_Types.Select(tt => tt.Name).ToList();
            ViewBag.ConditionTypes = db.Condition_Types.Select(ct => ct.Name).ToList();


            string priceFrom = Request.QueryString["priceFrom"];
            string priceTo = Request.QueryString["priceTo"];
            string yearFrom = Request.QueryString["yearFrom"];
            string yearTo = Request.QueryString["yearTo"];
            string kilometersFrom = Request.QueryString["kilometersFrom"];
            string kilometersTo = Request.QueryString["kilometersTo"];
            string kWFrom = Request.QueryString["kWFrom"];
            string kWTo = Request.QueryString["kWTo"];

            var selectedFuelTypes = (Request.QueryString["fuelTypes"]?.Split(',') ?? new string[0])
                            .Where(s => !string.Equals(s, "false", StringComparison.OrdinalIgnoreCase))
                            .ToArray();
            var selectedBodyTypes = (Request.QueryString["bodyTypes"]?.Split(',') ?? new string[0])
                                    .Where(s => !string.Equals(s, "false", StringComparison.OrdinalIgnoreCase))
                                    .ToArray();
            var selectedConditionTypes = (Request.QueryString["conditionTypes"]?.Split(',') ?? new string[0])
                                        .Where(s => !string.Equals(s, "false", StringComparison.OrdinalIgnoreCase))
                                        .ToArray();
            var selectedTransmissionTypes = (Request.QueryString["transmitionTypes"]?.Split(',') ?? new string[0])
                                            .Where(s => !string.Equals(s, "false", StringComparison.OrdinalIgnoreCase))
                                            .ToArray();
            var selectedCities = (Request.QueryString["selectedCities"]?.Split(',') ?? new string[0])
                                      .Where(s => !string.Equals(s, "false", StringComparison.OrdinalIgnoreCase))
                                      .ToArray();

            var listingsQuery = db.Listings.Where(l => l.Approved == true).Include(l => l.User).Where(l => l.Approved).OrderByDescending(l => l.Created).AsQueryable();
            listingsQuery = listingsQuery
                .Where(l =>
                    (!selectedFuelTypes.Any() || selectedFuelTypes.Contains(l.Car.Fuel_Type)) &&
                    (!selectedBodyTypes.Any() || selectedBodyTypes.Contains(l.Car.Body_Type)) &&
                    (!selectedConditionTypes.Any() || selectedConditionTypes.Contains(l.Condition)) &&
                    (!selectedTransmissionTypes.Any() || selectedTransmissionTypes.Contains(l.Car.Transmition)) &&
                    (!selectedCities.Any() || selectedCities.Contains(l.User.City))
                );

            var listingsList = listingsQuery.ToList();

            
            var filteredListings = listingsList
                .Where(l => (string.IsNullOrEmpty(priceFrom) || CompareNumericStrings(l.Price, priceFrom) >= 0) &&
                            (string.IsNullOrEmpty(priceTo) || CompareNumericStrings(l.Price, priceTo) <= 0) &&
                            (string.IsNullOrEmpty(yearFrom) || CompareNumericStrings(l.Car.Registration_Year, yearFrom) >= 0) &&
                            (string.IsNullOrEmpty(yearTo) || CompareNumericStrings(l.Car.Registration_Year, yearTo) <= 0) &&
                            (string.IsNullOrEmpty(kilometersFrom) || CompareNumericStrings(l.Car.Kilometers, kilometersFrom) >= 0) &&
                            (string.IsNullOrEmpty(kilometersTo) || CompareNumericStrings(l.Car.Kilometers, kilometersTo) <= 0) &&
                            (string.IsNullOrEmpty(kWFrom) || CompareNumericStrings(l.Car.Kilowatts, kWFrom) >= 0) &&
                            (string.IsNullOrEmpty(kWTo) || CompareNumericStrings(l.Car.Kilowatts, kWTo) <= 0) /*&& 
                            (selectedFuelTypes == null || selectedFuelTypes.Contains(l.Car.Fuel_Type)) &&
                            (selectedBodyTypes == null || selectedBodyTypes.Contains(l.Car.Body_Type)) &&
                            (selectedConditionTypes == null || selectedConditionTypes.Contains(l.Condition)) &&
                            (selectedTransmissionTypes == null || selectedTransmissionTypes.Contains(l.Car.Transmition))*/
                            ).ToList();
            var comparer = new NumericStringComparer(CompareNumericStrings);
            var pagedListings = filteredListings.OrderBy(l => l.Created).ToPagedList(pageNumber, pageSize);
            string sort = Request.QueryString["sortBy"];
            if (sort != null)
            {
                if (sort.Equals("date_asc"))
                {
                    //var pagedListings = filteredListings.OrderBy(l => l.Created).ToPagedList(pageNumber, pageSize);
                }
                else if (sort.Equals("date_desc"))
                {
                    pagedListings = filteredListings.OrderByDescending(l => l.Created).ToPagedList(pageNumber, pageSize);
                }
                else if (sort.Equals("price_asc"))
                {
                    pagedListings = filteredListings.OrderBy(l => l.Price, comparer).ToPagedList(pageNumber, pageSize);
                }
                else
                {
                    pagedListings = filteredListings.OrderByDescending(l => l.Price, comparer).ToPagedList(pageNumber, pageSize);
                }
            }
            else
            {
                pagedListings = filteredListings.OrderByDescending(l => l.Created).ToPagedList(pageNumber, pageSize);
            }
            return View("Index", pagedListings);
        }
        [AllowAnonymous]
        [HttpGet]
        public ActionResult Search(string SearchQuery, int? page)
        {
            
            ViewBag.FuelTypes =  db.Fuel_Types.Select(ft => ft.Name).ToList();
            ViewBag.BodyTypes = db.Body_Types.Select(bt => bt.Name).ToList();
            ViewBag.TransmitionTypes = db.Transmission_Types.Select(tt => tt.Name).ToList();
            ViewBag.ConditionTypes = db.Condition_Types.Select(ct => ct.Name).ToList();
            int pageSize = 8;
            int pageNumber = (page ?? 1);
            string query = SearchQuery;
            if (string.IsNullOrEmpty(query))
            {
                return RedirectToAction("Index", "Home");
            }
            else
            {
                var listings = db.Listings.Where(l => l.Approved == true).Where(l => l.Description.Contains(query) || l.Title.Contains(query)).Include(l => l.User).OrderByDescending(l => l.Created).ToPagedList(pageNumber, pageSize); ;
                return View("Index", listings);
            }
        }
        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }
    }
}
