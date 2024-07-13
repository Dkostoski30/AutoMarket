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
    public class Condition_TypeController : Controller
    {
        private ApplicationDbContext db = new ApplicationDbContext();

        // GET: Condition_Type
        public ActionResult Index()
        {
            return View(db.Condition_Types.ToList());
        }

        // GET: Condition_Type/Create
        public ActionResult Create()
        {
            return View();
        }

        // POST: Condition_Type/Create
        // To protect from overposting attacks, enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Create([Bind(Include = "ID,Name")] Condition_Type condition_Type)
        {
            if (ModelState.IsValid)
            {
                db.Condition_Types.Add(condition_Type);
                db.SaveChanges();
                return RedirectToAction("Index");
            }

            return View(condition_Type);
        }

        // GET: Condition_Type/Edit/5
        public ActionResult Edit(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Condition_Type condition_Type = db.Condition_Types.Find(id);
            if (condition_Type == null)
            {
                return HttpNotFound();
            }
            return View(condition_Type);
        }

        // POST: Condition_Type/Edit/5
        // To protect from overposting attacks, enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Edit([Bind(Include = "ID,Name")] Condition_Type condition_Type)
        {
            if (ModelState.IsValid)
            {
                db.Entry(condition_Type).State = EntityState.Modified;
                db.SaveChanges();
                return RedirectToAction("Index");
            }
            return View(condition_Type);
        }

        public ActionResult Delete(int id)
        {
            Condition_Type condition_Type = db.Condition_Types.Find(id);
            db.Condition_Types.Remove(condition_Type);
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
