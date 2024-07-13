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
    public class Body_TypeController : Controller
    {
        private ApplicationDbContext db = new ApplicationDbContext();

        // GET: Body_Type
        public ActionResult Index()
        {
            return View(db.Body_Types.ToList());
        }

        // GET: Body_Type/Details/5
        public ActionResult Details(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Body_Type body_Type = db.Body_Types.Find(id);
            if (body_Type == null)
            {
                return HttpNotFound();
            }
            return View(body_Type);
        }

        // GET: Body_Type/Create
        public ActionResult Create()
        {
            return View();
        }

        // POST: Body_Type/Create
        // To protect from overposting attacks, enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Create([Bind(Include = "ID,Name")] Body_Type body_Type)
        {
            if (ModelState.IsValid)
            {
                db.Body_Types.Add(body_Type);
                db.SaveChanges();
                return RedirectToAction("Index");
            }

            return View(body_Type);
        }

        // GET: Body_Type/Edit/5
        public ActionResult Edit(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Body_Type body_Type = db.Body_Types.Find(id);
            if (body_Type == null)
            {
                return HttpNotFound();
            }
            return View(body_Type);
        }

        // POST: Body_Type/Edit/5
        // To protect from overposting attacks, enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Edit([Bind(Include = "ID,Name")] Body_Type body_Type)
        {
            if (ModelState.IsValid)
            {
                db.Entry(body_Type).State = EntityState.Modified;
                db.SaveChanges();
                return RedirectToAction("Index");
            }
            return View(body_Type);
        }

        // GET: Body_Type/Delete/5
        public ActionResult Delete(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Body_Type body_Type = db.Body_Types.Find(id);
            if (body_Type == null)
            {
                return HttpNotFound();
            }
            return View(body_Type);
        }

        // POST: Body_Type/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public ActionResult DeleteConfirmed(int id)
        {
            Body_Type body_Type = db.Body_Types.Find(id);
            db.Body_Types.Remove(body_Type);
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
