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
    public class Transmission_TypeController : Controller
    {
        private ApplicationDbContext db = new ApplicationDbContext();

        // GET: Transmission_Type
        public ActionResult Index()
        {
            return View(db.Transmission_Types.ToList());
        }


        // GET: Transmission_Type/Create
        public ActionResult Create()
        {
            return View();
        }

        // POST: Transmission_Type/Create
        // To protect from overposting attacks, enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Create([Bind(Include = "ID,Name")] Transmission_Type transmission_Type)
        {
            if (ModelState.IsValid)
            {
                db.Transmission_Types.Add(transmission_Type);
                db.SaveChanges();
                return RedirectToAction("Index");
            }

            return View(transmission_Type);
        }

        // GET: Transmission_Type/Edit/5
        public ActionResult Edit(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Transmission_Type transmission_Type = db.Transmission_Types.Find(id);
            if (transmission_Type == null)
            {
                return HttpNotFound();
            }
            return View(transmission_Type);
        }

        // POST: Transmission_Type/Edit/5
        // To protect from overposting attacks, enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Edit([Bind(Include = "ID,Name")] Transmission_Type transmission_Type)
        {
            if (ModelState.IsValid)
            {
                db.Entry(transmission_Type).State = EntityState.Modified;
                db.SaveChanges();
                return RedirectToAction("Index");
            }
            return View(transmission_Type);
        }

  
        public ActionResult Delete(int id)
        {
            Transmission_Type transmission_Type = db.Transmission_Types.Find(id);
            db.Transmission_Types.Remove(transmission_Type);
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
