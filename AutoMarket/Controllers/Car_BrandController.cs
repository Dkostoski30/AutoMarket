using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.Mvc;
using AutoMarket.Models;

namespace AutoMarket.Controllers
{
    public class Car_BrandController : Controller
    {
        private ApplicationDbContext db = new ApplicationDbContext();

        // GET: Car_Brand
        public ActionResult Index()
        {
            return View(db.Car_Brands.ToList());
        }

        // GET: Car_Brand/Details/5
        public ActionResult Details(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Car_Brand car_Brand = db.Car_Brands.Find(id);
            if (car_Brand == null)
            {
                return HttpNotFound();
            }
            return View(car_Brand);
        }

        // GET: Car_Brand/Create
        public ActionResult Create()
        {
            return View();
        }

        // POST: Car_Brand/Create
        // To protect from overposting attacks, enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Create([Bind(Include = "Id,Name")] Car_Brand car_Brand)
        {
            if (ModelState.IsValid)
            {
                db.Car_Brands.Add(car_Brand);
                db.SaveChanges();
                return RedirectToAction("Index");
            }

            return View(car_Brand);
        }

        // GET: Car_Brand/Edit/5
        public ActionResult Edit(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Car_Brand car_Brand = db.Car_Brands.Find(id);
            if (car_Brand == null)
            {
                return HttpNotFound();
            }
            return View(car_Brand);
        }

        // POST: Car_Brand/Edit/5
        // To protect from overposting attacks, enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Edit([Bind(Include = "Id,Name")] Car_Brand car_Brand)
        {
            if (ModelState.IsValid)
            {
                db.Entry(car_Brand).State = EntityState.Modified;
                db.SaveChanges();
                return RedirectToAction("Index");
            }
            return View(car_Brand);
        }

        // GET: Car_Brand/Delete/5
        public ActionResult Delete(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Car_Brand car_Brand = db.Car_Brands.Find(id);
            if (car_Brand == null)
            {
                return HttpNotFound();
            }
            return View(car_Brand);
        }

        // POST: Car_Brand/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public ActionResult DeleteConfirmed(int id)
        {
            Car_Brand car_Brand = db.Car_Brands.Find(id);
            db.Car_Brands.Remove(car_Brand);
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
