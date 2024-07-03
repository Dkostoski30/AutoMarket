using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.IO;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.Mvc;
using AutoMarket.Models;
using Microsoft.AspNet.Identity;

namespace AutoMarket.Controllers
{
   
    public class ListingsController : Controller
    {
        private ApplicationDbContext db = new ApplicationDbContext();

        // GET: Listings
        [AllowAnonymous]
        public ActionResult Index()
        {
            var listings = db.Listings.Include(l => l.User).ToList();
            ViewBag.FuelTypes = new List<string> { "Petrol", "Diesel", "Electric", "Hybrid", "Hydrogen" };
            ViewBag.BodyTypes = new List<string> { "Sedan", "SUV", "Hatchback", "Coupe", "Convertible", "Minivan" };
            ViewBag.TransmitionTypes = new List<string> { "Manual", "Automatic", "Semi-Automatic", "CVT"};
            ViewBag.ConditionTypes = new List<string> { "New", "Used"};

            return View(listings);
        }
       
        [AllowAnonymous]
        public ActionResult FilterBodyType(int id)
        {

            ViewBag.FuelTypes = new List<string> { "Petrol", "Diesel", "Electric", "Hybrid", "Hydrogen" };
            ViewBag.BodyTypes = new List<string> { "Sedan", "SUV", "Hatchback", "Coupe", "Convertible", "Minivan" };
            ViewBag.TransmitionTypes = new List<string> { "Manual", "Automatic", "Semi-Automatic", "CVT" };
            ViewBag.ConditionTypes = new List<string> { "New", "Used" };
            if (id == 1)
            {
                var listings = db.Listings.Include(l => l.User).Where(l => l.Car.Body_Type == "SUV").ToList();
                return View("Index", listings);
            }
            return RedirectToAction("Index", "Home");
            
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
        [AllowAnonymous]
        [HttpGet]
        public ActionResult ApplyFilters()
        {
            //TODO: Implementiraj go ovaj del

            return RedirectToAction("/Home/Index");
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
