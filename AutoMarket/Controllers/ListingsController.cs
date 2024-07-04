﻿using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Drawing.Printing;
using System.IO;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.Mvc;
using System.Web.UI;
using AutoMarket.Models;
using Microsoft.Ajax.Utilities;
using Microsoft.AspNet.Identity;
using PagedList;

namespace AutoMarket.Controllers
{
   
    public class ListingsController : Controller
    {
        private ApplicationDbContext db = new ApplicationDbContext();

        // GET: Listings
        [AllowAnonymous]
        public ActionResult Index(int? page)
        {
            
            int pageSize = 8; // Number of items per page
            int pageNumber = (page ?? 1);

            var listingsPaged = db.Listings.Include(l => l.User).OrderBy(l => l.ID).ToPagedList(pageNumber, pageSize);
            ViewBag.FuelTypes = new List<string> { "Petrol", "Diesel", "Electric", "Hybrid", "Hydrogen" };
            ViewBag.BodyTypes = new List<string> { "Sedan", "SUV", "Hatchback", "Coupe", "Convertible", "Minivan" };
            ViewBag.TransmitionTypes = new List<string> { "Manual", "Automatic", "Semi-Automatic", "CVT"};
            ViewBag.ConditionTypes = new List<string> { "New", "Used"};

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
            ViewBag.BodyTypes = new List<string> { "Sedan", "SUV", "Hatchback", "Coupe", "Convertible", "Minivan" };
            ViewBag.TransmitionTypes = new List<string> { "Manual", "Automatic", "Semi-Automatic", "CVT" };
            ViewBag.ConditionTypes = new List<string> { "New", "Used" };
            if (id == 1)
            {
                var listings = db.Listings.Include(l => l.User).Where(l => l.Car.Body_Type == "SUV").OrderBy(l => l.ID).ToPagedList(pageNumber, pageSize);
                return View("Index", listings);
            }
            else if (id == 2)
            {
                var listings = db.Listings.Include(l => l.User).Where(l => l.Car.Body_Type == "Sedan").OrderBy(l => l.ID).ToPagedList(pageNumber, pageSize);
                return View("Index", listings);

            }
            else if(id == 3)
            {
                var listings = db.Listings.Include(l => l.User).Where(l => l.Car.Body_Type == "Hatchback").OrderBy(l => l.ID).ToPagedList(pageNumber, pageSize);
                return View("Index", listings);

            }else if(id == 4)
            {
                var listings = db.Listings.Include(l => l.User).Where(l => l.Car.Body_Type == "Coupe").OrderBy(l => l.ID).ToPagedList(pageNumber, pageSize);
                return View("Index", listings);

            }
            else
            {
                var listings = db.Listings.Include(l => l.User).OrderBy(l => l.ID).ToPagedList(pageNumber, pageSize);
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
        public ActionResult Create([Bind(Include = "ID,Car,Price,Title,Description,Created")] Listing listing)
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
        public ActionResult Edit([Bind(Include = "ID,Car,Price,Title,Description,Created")] Listing listing)
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
        public ActionResult Delete(int? id)
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

        // POST: Listings/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public ActionResult DeleteConfirmed(int id)
        {
            Listing listing = db.Listings.Find(id);
            db.Images.Where(i => i.Listing_ID == id).ToList();
            db.Listings.Remove(listing);
            db.SaveChanges();
            return RedirectToAction("Index");
        }
        private int CompareNumericStrings(string numStr1, string numStr2)
        {
            // Remove leading zeros for accurate length comparison
            numStr1 = numStr1.TrimStart('0');
            numStr2 = numStr2.TrimStart('0');

            // Compare lengths
            if (numStr1.Length > numStr2.Length)
                return 1; // numStr1 is greater
            if (numStr1.Length < numStr2.Length)
                return -1; // numStr2 is greater

            // If lengths are equal, compare lexicographically
            return string.Compare(numStr1, numStr2);
        }

        [AllowAnonymous]
        [HttpGet]
        public ActionResult ApplyFilters(int? page)
        {
           

            int pageSize = 8; // Number of items per page
            int pageNumber = 1;
            ViewBag.FuelTypes = new List<string> { "Petrol", "Diesel", "Electric", "Hybrid", "Hydrogen" };
            ViewBag.BodyTypes = new List<string> { "Sedan", "SUV", "Hatchback", "Coupe", "Convertible", "Minivan" };
            ViewBag.TransmitionTypes = new List<string> { "Manual", "Automatic", "Semi-Automatic", "CVT" };
            ViewBag.ConditionTypes = new List<string> { "New", "Used" };


            string priceFrom = Request.QueryString["priceFrom"];
            string priceTo = Request.QueryString["priceTo"];
            string yearFrom = Request.QueryString["yearFrom"];
            string yearTo = Request.QueryString["yearTo"];
            string kilometersFrom = Request.QueryString ["kilometersFrom"];
            string kilometersTo = Request.QueryString["kilometersTo"];
            string kWFrom = Request.QueryString["kWFrom"];
            string kWTo = Request.QueryString["kWTo"];


            //Default return of listings
            var listings = db.Listings.Include(l => l.User).OrderBy(l => l.ID).ToPagedList(pageNumber, pageSize);

            var listingsQuery = db.Listings.Include(l => l.User).OrderBy(l => l.ID);

            // Retrieve the listings as a list to perform in-memory filtering
            var listingsList = listingsQuery.ToList();

            // Apply in-memory filtering
            var filteredListings = listingsList
                .Where(l => (string.IsNullOrEmpty(priceFrom) || CompareNumericStrings(l.Price, priceFrom) >= 0) &&
                            (string.IsNullOrEmpty(priceTo) || CompareNumericStrings(l.Price, priceTo) <= 0) &&
                            (string.IsNullOrEmpty(yearFrom) || CompareNumericStrings(l.Car.Registration_Year, yearFrom) >= 0) &&
                            (string.IsNullOrEmpty(yearTo) || CompareNumericStrings(l.Car.Registration_Year, yearTo) <= 0) &&
                            (string.IsNullOrEmpty(kilometersFrom) || CompareNumericStrings(l.Car.Kilometers, kilometersFrom) >= 0) &&
                            (string.IsNullOrEmpty(kilometersTo) || CompareNumericStrings(l.Car.Kilometers, kilometersTo) <= 0) &&
                            (string.IsNullOrEmpty(kWFrom) || CompareNumericStrings(l.Car.Kilowatts, kWFrom) >= 0) &&
                            (string.IsNullOrEmpty(kWTo) || CompareNumericStrings(l.Car.Kilowatts, kWTo) <= 0))
                .ToList(); // Convert to list after filtering

            // Convert the filtered list to a paged list
            var pagedListings = filteredListings.ToPagedList(pageNumber, pageSize);

            return View("Index", pagedListings);
        }
        [AllowAnonymous]
        [HttpGet]
        public ActionResult Search(string SearchQuery)
        {
            
            ViewBag.FuelTypes = new List<string> { "Petrol", "Diesel", "Electric", "Hybrid", "Hydrogen" };
            ViewBag.BodyTypes = new List<string> { "Sedan", "SUV", "Hatchback", "Coupe", "Convertible", "Minivan" };
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
                var listings = db.Listings.Where(l => l.Description.Contains(query) || l.Title.Contains(query)).Include(l => l.User).OrderBy(l => l.ID).ToPagedList(pageNumber, pageSize); ;
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
