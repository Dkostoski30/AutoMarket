using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.Mvc;
using AutoMarket.Models;
using PagedList;

namespace AutoMarket.Controllers
{
    public class UserController : Controller
    {
        private ApplicationDbContext db = new ApplicationDbContext();
        // GET: User
        [Authorize(Roles = "Admin")]
        public ActionResult Index()
        {
            ViewBag.Roles = db.Roles.ToList();
            return View(db.Users.ToList());
        }

        // GET: User/Details/5
        [AllowAnonymous]
        public ActionResult Details(string id, int? page)
        {
            int pageNumber = (page ?? 1);
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            ApplicationUser applicationUser = db.Users.Find(id);

            if (applicationUser == null)
            {
                return HttpNotFound();
            }
            ViewBag.PageNumber = pageNumber;
            ViewBag.Listings = db.Listings.Where(l => l.User.Email.Equals(applicationUser.Email) && l.Approved == true).OrderBy(l => l.Created).ToPagedList(pageNumber, 4);
            return View(applicationUser);
        }

        // GET: User/Create
        public ActionResult Create()
        {
            return RedirectToAction("Register", "Account");
        }

        // POST: User/Create
        // To protect from overposting attacks, enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.
        /*[HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Create([Bind(Include = "Id,PhoneNumber,City,Name,Email,EmailConfirmed,PasswordHash,SecurityStamp,PhoneNumberConfirmed,TwoFactorEnabled,LockoutEndDateUtc,LockoutEnabled,AccessFailedCount,UserName")] ApplicationUser applicationUser)
        {
            if (ModelState.IsValid)
            {
                db.Users.Add(applicationUser);
                db.SaveChanges();
                return RedirectToAction("Index");
            }

            return View(applicationUser);
        }*/

        // GET: User/Edit/5
        [Authorize(Roles = "Admin")]
        public ActionResult Edit(string id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            ApplicationUser applicationUser = db.Users.Find(id);
            if (applicationUser == null)
            {
                return HttpNotFound();
            }
            return View(applicationUser);
        }

        // POST: User/Edit/5
        // To protect from overposting attacks, enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.
        [Authorize(Roles = "Admin")]
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Edit([Bind(Include = "Id,PhoneNumber,City,Name,Email,EmailConfirmed,PasswordHash,SecurityStamp,PhoneNumberConfirmed,TwoFactorEnabled,LockoutEndDateUtc,LockoutEnabled,AccessFailedCount,UserName")] ApplicationUser applicationUser)
        {
            if (ModelState.IsValid)
            {
                db.Entry(applicationUser).State = EntityState.Modified;
                db.SaveChanges();
                return RedirectToAction("Index");
            }
            return View(applicationUser);
        }

        // GET: User/Delete/5
        /*[Authorize(Roles = "Admin")]
        public ActionResult Delete(string id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            ApplicationUser applicationUser = db.Users.Find(id);
            if (applicationUser == null)
            {
                return HttpNotFound();
            }
            return View(applicationUser);
        }*/

        // POST: User/Delete/5
   
        public ActionResult Delete(string id)
        {
            ApplicationUser applicationUser = db.Users.Find(id);
            var listings = db.Listings.Where(l => l.User.Email.Equals(applicationUser.Email)).ToList();
            foreach(var item in listings)
            {
                var images = db.Images.Where(i => i.Listing_ID == item.ID).ToList();
                foreach (var image in images)
                {
                    string filePath = image.Path;
                    if (System.IO.File.Exists(filePath))
                    {
                        System.IO.File.Delete(filePath);
                    }
                    db.Images.Remove(image);
                }
                db.Listings.Remove(item);
            }
            db.Users.Remove(applicationUser);
            db.SaveChanges();
            return RedirectToAction("Index");
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
