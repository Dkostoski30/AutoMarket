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
    public class Fuel_TypeController : Controller
    {
        private ApplicationDbContext db = new ApplicationDbContext();

        // GET: Fuel_Type
        public ActionResult Index()
        {
            return View(db.Fuel_Types.ToList());
        }

        // GET: Fuel_Type/Details/5
        public ActionResult Details(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Fuel_Type fuel_Type = db.Fuel_Types.Find(id);
            if (fuel_Type == null)
            {
                return HttpNotFound();
            }
            return View(fuel_Type);
        }

        // GET: Fuel_Type/Create
        public ActionResult Create()
        {
            return View();
        }

        // POST: Fuel_Type/Create
        // To protect from overposting attacks, enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Create([Bind(Include = "ID,Name")] Fuel_Type fuel_Type)
        {
            if (ModelState.IsValid)
            {
                db.Fuel_Types.Add(fuel_Type);
                db.SaveChanges();
                return RedirectToAction("Index");
            }

            return View(fuel_Type);
        }

        // GET: Fuel_Type/Edit/5
        public ActionResult Edit(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Fuel_Type fuel_Type = db.Fuel_Types.Find(id);
            if (fuel_Type == null)
            {
                return HttpNotFound();
            }
            return View(fuel_Type);
        }

        // POST: Fuel_Type/Edit/5
        // To protect from overposting attacks, enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Edit([Bind(Include = "ID,Name")] Fuel_Type fuel_Type)
        {
            if (ModelState.IsValid)
            {
                db.Entry(fuel_Type).State = EntityState.Modified;
                db.SaveChanges();
                return RedirectToAction("Index");
            }
            return View(fuel_Type);
        }



        public ActionResult Delete(int id)
        {
            Fuel_Type fuel_Type = db.Fuel_Types.Find(id);
            db.Fuel_Types.Remove(fuel_Type);
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
