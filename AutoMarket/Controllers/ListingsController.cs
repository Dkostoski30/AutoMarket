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

            var listingsPaged = db.Listings.Include(l => l.User).OrderByDescending(l => l.Created).ToPagedList(pageNumber, pageSize);
            return View(listingsPaged);
        }
     /*   public ActionResult Index(ICollection<Listing> listings)
        {
            ViewBag.FuelTypes = new List<string> { "Petrol", "Diesel", "Electric", "Hybrid", "Hydrogen" };
            ViewBag.BodyTypes = new List<string> { "Sedan", "SUV", "Hatchback", "Coupe", "Convertible", "Minivan" };
            ViewBag.TransmitionTypes = new List<string> { "Manual", "Automatic", "Semi-Automatic", "CVT" };
            ViewBag.ConditionTypes = new List<string> { "New", "Used" };

            return View(listings);
        }*/
        [AllowAnonymous]
        public ActionResult FilterBodyType(int id)
        {
            int pageSize = 8; // Number of items per page
            int pageNumber = 1;
            ViewBag.FuelTypes = new List<string> { "Petrol", "Diesel", "Electric", "Hybrid", "Hydrogen" };
            ViewBag.BodyTypes = new List<string> { "Sedan", "Wagon", "SUV", "Hatchback", "Coupe", "Convertible", "Minivan" };
            ViewBag.TransmitionTypes = new List<string> { "Manual", "Automatic", "Semi-Automatic", "CVT" };
            ViewBag.ConditionTypes = new List<string> { "New", "Used" };
            if (id == 1)
            {
                var listings = db.Listings.Include(l => l.User).Where(l => l.Car.Body_Type == "SUV").OrderByDescending(l => l.Created).ToPagedList(pageNumber, pageSize);
                return View("Index", listings);
            }
            else if (id == 2)
            {
                var listings = db.Listings.Include(l => l.User).Where(l => l.Car.Body_Type == "Sedan").OrderByDescending(l => l.Created).ToPagedList(pageNumber, pageSize);
                return View("Index", listings);

            }
            else if(id == 3)
            {
                var listings = db.Listings.Include(l => l.User).Where(l => l.Car.Body_Type == "Hatchback").OrderByDescending(l => l.Created).ToPagedList(pageNumber, pageSize);
                return View("Index", listings);

            }else if(id == 4)
            {
                var listings = db.Listings.Include(l => l.User).Where(l => l.Car.Body_Type == "Coupe").OrderByDescending(l => l.Created).ToPagedList(pageNumber, pageSize);
                return View("Index", listings);

            }
            else
            {
                var listings = db.Listings.Include(l => l.User).OrderByDescending(l => l.Created).ToPagedList(pageNumber, pageSize);
                return View("Index", listings);

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
            ViewBag.FuelTypes = new SelectList(new[] {
                new { Value = "Diesel", Text = "Diesel" },
                new { Value = "Petrol", Text = "Petrol" },
                new { Value = "Hybrid", Text = "Hybrid" },
                new { Value = "Electric", Text = "Electric" },
                new { Value = "Hydrogen", Text = "Hydrogen" }
               },          "Value", "Text");
            ViewBag.CarBrands = new SelectList(new[]
            {
    new { Value = "Abarth", Text = "Abarth" },
    new { Value = "Alfa Romeo", Text = "Alfa Romeo" },
    new { Value = "Aston Martin", Text = "Aston Martin" },
    new { Value = "Audi", Text = "Audi" },
    new { Value = "Bentley", Text = "Bentley" },
    new { Value = "BMW", Text = "BMW" },
    new { Value = "Bugatti", Text = "Bugatti" },
    new { Value = "Cadillac", Text = "Cadillac" },
    new { Value = "Chevrolet", Text = "Chevrolet" },
    new { Value = "Chrysler", Text = "Chrysler" },
    new { Value = "Citroën", Text = "Citroën" },
    new { Value = "Dacia", Text = "Dacia" },
    new { Value = "Daewoo", Text = "Daewoo" },
    new { Value = "Daihatsu", Text = "Daihatsu" },
    new { Value = "Dodge", Text = "Dodge" },
    new { Value = "Donkervoort", Text = "Donkervoort" },
    new { Value = "DS", Text = "DS" },
    new { Value = "Ferrari", Text = "Ferrari" },
    new { Value = "Fiat", Text = "Fiat" },
    new { Value = "Fisker", Text = "Fisker" },
    new { Value = "Ford", Text = "Ford" },
    new { Value = "Honda", Text = "Honda" },
    new { Value = "Hummer", Text = "Hummer" },
    new { Value = "Hyundai", Text = "Hyundai" },
    new { Value = "Infiniti", Text = "Infiniti" },
    new { Value = "Iveco", Text = "Iveco" },
    new { Value = "Jaguar", Text = "Jaguar" },
    new { Value = "Jeep", Text = "Jeep" },
    new { Value = "Kia", Text = "Kia" },
    new { Value = "KTM", Text = "KTM" },
    new { Value = "Lada", Text = "Lada" },
    new { Value = "Lamborghini", Text = "Lamborghini" },
    new { Value = "Lancia", Text = "Lancia" },
    new { Value = "Land Rover", Text = "Land Rover" },
    new { Value = "Landwind", Text = "Landwind" },
    new { Value = "Lexus", Text = "Lexus" },
    new { Value = "Lotus", Text = "Lotus" },
    new { Value = "Maserati", Text = "Maserati" },
    new { Value = "Maybach", Text = "Maybach" },
    new { Value = "Mazda", Text = "Mazda" },
    new { Value = "McLaren", Text = "McLaren" },
    new { Value = "Mercedes-Benz", Text = "Mercedes-Benz" },
    new { Value = "MG", Text = "MG" },
    new { Value = "Mini", Text = "Mini" },
    new { Value = "Mitsubishi", Text = "Mitsubishi" },
    new { Value = "Morgan", Text = "Morgan" },
    new { Value = "Nissan", Text = "Nissan" },
    new { Value = "Opel", Text = "Opel" },
    new { Value = "Peugeot", Text = "Peugeot" },
    new { Value = "Porsche", Text = "Porsche" },
    new { Value = "Renault", Text = "Renault" },
    new { Value = "Rolls-Royce", Text = "Rolls-Royce" },
    new { Value = "Rover", Text = "Rover" },
    new { Value = "Saab", Text = "Saab" },
    new { Value = "Seat", Text = "Seat" },
    new { Value = "Skoda", Text = "Skoda" },
    new { Value = "Smart", Text = "Smart" },
    new { Value = "SsangYong", Text = "SsangYong" },
    new { Value = "Subaru", Text = "Subaru" },
    new { Value = "Suzuki", Text = "Suzuki" },
    new { Value = "Tesla", Text = "Tesla" },
    new { Value = "Toyota", Text = "Toyota" },
    new { Value = "Volkswagen", Text = "Volkswagen" },
    new { Value = "Volvo", Text = "Volvo" }
}, "Value", "Text");
            ViewBag.TransmissionTypes = new SelectList(new[]
             {
                    new { Value = "Manual", Text = "Manual" },
                    new { Value = "Automatic", Text = "Automatic" },
                    new { Value = "Semi-Automatic", Text = "Semi-Automatic" },
                    new { Value = "CVT", Text = "CVT" }
                }, "Value", "Text");
            ViewBag.BodyTypes = new SelectList(new[]
              {
                  new { Value = "Sedan", Text = "Sedan" },
                  new { Value = "Wagon", Text = "Wagon" },
                  new { Value = "SUV", Text = "SUV" },
                  new { Value = "Hatchback", Text = "Hatchback" },
                  new { Value = "Coupe", Text = "Coupe" },
                  new { Value = "Convertible", Text = "Convertible" }
              }, "Value", "Text");
            
            return View();
        }

        // POST: Listings/Create
        // To protect from overposting attacks, enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Create([Bind(Include = "ID,Car,Price,Title,Description,Created,Condition")] Listing listing)
        {
            if (ModelState.IsValid)
            {
                /*HttpPostedFileBase FrontImageBase = Request.Files["FrontImage"];
                string front_image_name = Path.GetFileName(FrontImageBase.FileName);
                string front_image_path = Path.Combine(Server.MapPath("~/Content/Images/car_images/"), front_image_name);
                Image FrontImage = new Image(front_image_name, front_image_path);
                FrontImageBase.SaveAs(front_image_path);
                listing.Images.Add(FrontImage);
                //
                HttpPostedFileBase BackImageBase = Request.Files["BackImage"];
                string back_image_name = Path.GetFileName(BackImageBase.FileName);
                string back_image_path = Path.Combine(Server.MapPath("~/Content/Images/car_images/"), back_image_name);
                Image BackImage = new Image(back_image_name, back_image_path);
                BackImageBase.SaveAs(back_image_path);
                listing.Images.Add(BackImage);

                HttpPostedFileBase SideImageBase = Request.Files["SideImage"];
                string side_image_name = Path.GetFileName(SideImageBase.FileName);
                string side_image_path = Path.Combine(Server.MapPath("~/Content/Images/car_images/"), side_image_name);
                Image SideImage = new Image(side_image_name, side_image_path);
                SideImageBase.SaveAs(side_image_path);
                listing.Images.Add(SideImage);

                HttpPostedFileBase InteriorImageBase = Request.Files["InteriorImage"];
                string interior_image_name = Path.GetFileName(InteriorImageBase.FileName);
                string interior_image_path = Path.Combine(Server.MapPath("~/Content/Images/car_images/"), interior_image_name);
                Image InteriorImage = new Image(interior_image_name, interior_image_path);
                InteriorImageBase.SaveAs(interior_image_path);
                listing.Images.Add(InteriorImage);
*/
                listing.Created = DateTime.Now;

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
                    // Set the listing's user to the current user
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
            ViewBag.FuelTypes = new SelectList(new[] {
                new { Value = "Diesel", Text = "Diesel" },
                new { Value = "Petrol", Text = "Petrol" },
                new { Value = "Hybrid", Text = "Hybrid" },
                new { Value = "Electric", Text = "Electric" },
                new { Value = "Hydrogen", Text = "Hydrogen" }
               }, "Value", "Text");
            ViewBag.CarBrands = new SelectList(new[]
            {
    new { Value = "Abarth", Text = "Abarth" },
    new { Value = "Alfa Romeo", Text = "Alfa Romeo" },
    new { Value = "Aston Martin", Text = "Aston Martin" },
    new { Value = "Audi", Text = "Audi" },
    new { Value = "Bentley", Text = "Bentley" },
    new { Value = "BMW", Text = "BMW" },
    new { Value = "Bugatti", Text = "Bugatti" },
    new { Value = "Cadillac", Text = "Cadillac" },
    new { Value = "Chevrolet", Text = "Chevrolet" },
    new { Value = "Chrysler", Text = "Chrysler" },
    new { Value = "Citroën", Text = "Citroën" },
    new { Value = "Dacia", Text = "Dacia" },
    new { Value = "Daewoo", Text = "Daewoo" },
    new { Value = "Daihatsu", Text = "Daihatsu" },
    new { Value = "Dodge", Text = "Dodge" },
    new { Value = "Donkervoort", Text = "Donkervoort" },
    new { Value = "DS", Text = "DS" },
    new { Value = "Ferrari", Text = "Ferrari" },
    new { Value = "Fiat", Text = "Fiat" },
    new { Value = "Fisker", Text = "Fisker" },
    new { Value = "Ford", Text = "Ford" },
    new { Value = "Honda", Text = "Honda" },
    new { Value = "Hummer", Text = "Hummer" },
    new { Value = "Hyundai", Text = "Hyundai" },
    new { Value = "Infiniti", Text = "Infiniti" },
    new { Value = "Iveco", Text = "Iveco" },
    new { Value = "Jaguar", Text = "Jaguar" },
    new { Value = "Jeep", Text = "Jeep" },
    new { Value = "Kia", Text = "Kia" },
    new { Value = "KTM", Text = "KTM" },
    new { Value = "Lada", Text = "Lada" },
    new { Value = "Lamborghini", Text = "Lamborghini" },
    new { Value = "Lancia", Text = "Lancia" },
    new { Value = "Land Rover", Text = "Land Rover" },
    new { Value = "Landwind", Text = "Landwind" },
    new { Value = "Lexus", Text = "Lexus" },
    new { Value = "Lotus", Text = "Lotus" },
    new { Value = "Maserati", Text = "Maserati" },
    new { Value = "Maybach", Text = "Maybach" },
    new { Value = "Mazda", Text = "Mazda" },
    new { Value = "McLaren", Text = "McLaren" },
    new { Value = "Mercedes-Benz", Text = "Mercedes-Benz" },
    new { Value = "MG", Text = "MG" },
    new { Value = "Mini", Text = "Mini" },
    new { Value = "Mitsubishi", Text = "Mitsubishi" },
    new { Value = "Morgan", Text = "Morgan" },
    new { Value = "Nissan", Text = "Nissan" },
    new { Value = "Opel", Text = "Opel" },
    new { Value = "Peugeot", Text = "Peugeot" },
    new { Value = "Porsche", Text = "Porsche" },
    new { Value = "Renault", Text = "Renault" },
    new { Value = "Rolls-Royce", Text = "Rolls-Royce" },
    new { Value = "Rover", Text = "Rover" },
    new { Value = "Saab", Text = "Saab" },
    new { Value = "Seat", Text = "Seat" },
    new { Value = "Skoda", Text = "Skoda" },
    new { Value = "Smart", Text = "Smart" },
    new { Value = "SsangYong", Text = "SsangYong" },
    new { Value = "Subaru", Text = "Subaru" },
    new { Value = "Suzuki", Text = "Suzuki" },
    new { Value = "Tesla", Text = "Tesla" },
    new { Value = "Toyota", Text = "Toyota" },
    new { Value = "Volkswagen", Text = "Volkswagen" },
    new { Value = "Volvo", Text = "Volvo" }
}, "Value", "Text");
            ViewBag.TransmissionTypes = new SelectList(new[]
             {
                    new { Value = "Manual", Text = "Manual" },
                    new { Value = "Automatic", Text = "Automatic" },
                    new { Value = "Semi-Automatic", Text = "Semi-Automatic" },
                    new { Value = "CVT", Text = "CVT" }
                }, "Value", "Text");
            ViewBag.BodyTypes = new SelectList(new[]
              {
                  new { Value = "Sedan", Text = "Sedan" },
                  new { Value = "Wagon", Text = "Wagon" },
                  new { Value = "SUV", Text = "SUV" },
                  new { Value = "Hatchback", Text = "Hatchback" },
                  new { Value = "Coupe", Text = "Coupe" },
                  new { Value = "Convertible", Text = "Convertible" }
              }, "Value", "Text");
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
        [Authorize(Roles="Admin,Moderator")]
        public ActionResult ManageListings()
        {
            var listings = db.Listings.Include(l => l.User).ToList();
            return View(listings);
        }

        [AllowAnonymous]
        [HttpGet]
        public ActionResult ApplyFilters(int? page)
        {


            int pageSize = 8;
            int pageNumber = 1;
            ViewBag.FuelTypes = new List<string> { "Petrol", "Diesel", "Electric", "Hybrid", "Hydrogen" };
            ViewBag.BodyTypes = new List<string> { "Sedan", "Wagon", "SUV", "Hatchback", "Coupe", "Convertible", "Minivan" };
            ViewBag.TransmitionTypes = new List<string> { "Manual", "Automatic", "Semi-Automatic", "CVT" };
            ViewBag.ConditionTypes = new List<string> { "New", "Used" };


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


            var listingsQuery = db.Listings.Include(l => l.User).OrderByDescending(l => l.Created).AsQueryable();
            listingsQuery = listingsQuery
                .Where(l =>
                    (!selectedFuelTypes.Any() || selectedFuelTypes.Contains(l.Car.Fuel_Type)) &&
                    (!selectedBodyTypes.Any() || selectedBodyTypes.Contains(l.Car.Body_Type)) &&
                    (!selectedConditionTypes.Any() || selectedConditionTypes.Contains(l.Condition)) &&
                    (!selectedTransmissionTypes.Any() || selectedTransmissionTypes.Contains(l.Car.Transmition))
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
            if (sort.Equals("date_asc"))
            {
                //var pagedListings = filteredListings.OrderBy(l => l.Created).ToPagedList(pageNumber, pageSize);
            }
            else if (sort.Equals("date_desc"))
            {
                 pagedListings = filteredListings.OrderByDescending(l => l.Created).ToPagedList(pageNumber, pageSize);
            }else if (sort.Equals("price_asc"))
            {
                 pagedListings = filteredListings.OrderBy(l => l.Price, comparer).ToPagedList(pageNumber, pageSize);
            }
            else
            {
                 pagedListings = filteredListings.OrderByDescending(l => l.Price, comparer).ToPagedList(pageNumber, pageSize);
            }
            return View("Index", pagedListings);
        }
        [AllowAnonymous]
        [HttpGet]
        public ActionResult Search(string SearchQuery)
        {
            
            ViewBag.FuelTypes = new List<string> { "Petrol", "Diesel", "Electric", "Hybrid", "Hydrogen" };
            ViewBag.BodyTypes = new List<string> { "Sedan", "Wagon", "SUV", "Hatchback", "Coupe", "Convertible", "Minivan" };
            ViewBag.TransmitionTypes = new List<string> { "Manual", "Automatic", "Semi-Automatic", "CVT" };
            ViewBag.ConditionTypes = new List<string> { "New", "Used" };
            int pageSize = 8;
            int pageNumber = 1;
            string query = SearchQuery;
            if (string.IsNullOrEmpty(query))
            {
                return RedirectToAction("Index", "Home");
            }
            else
            {
                var listings = db.Listings.Where(l => l.Description.Contains(query) || l.Title.Contains(query)).Include(l => l.User).OrderByDescending(l => l.Created).ToPagedList(pageNumber, pageSize); ;
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
