﻿using System.Data.Entity;
using System.Security.Claims;
using System.Threading.Tasks;
using Microsoft.AspNet.Identity;
using Microsoft.AspNet.Identity.EntityFramework;

namespace AutoMarket.Models
{
    // You can add profile data for the user by adding more properties to your ApplicationUser class, please visit https://go.microsoft.com/fwlink/?LinkID=317594 to learn more.
    public class ApplicationUser : IdentityUser
    {
        public string PhoneNumber {  get; set; }
        public string City { get; set; }
        public string Name { get; set; }
        public async Task<ClaimsIdentity> GenerateUserIdentityAsync(UserManager<ApplicationUser> manager)
        {
            // Note the authenticationType must match the one defined in CookieAuthenticationOptions.AuthenticationType
            var userIdentity = await manager.CreateIdentityAsync(this, DefaultAuthenticationTypes.ApplicationCookie);
            // Add custom user claims here
            return userIdentity;
        }
    }

    public class ApplicationDbContext : IdentityDbContext<ApplicationUser>
    {
       
        public ApplicationDbContext()
            : base("DefaultConnection", throwIfV1Schema: false)
        {
        }

        public static ApplicationDbContext Create()
        {
            return new ApplicationDbContext();
        }

        public System.Data.Entity.DbSet<AutoMarket.Models.Listing> Listings { get; set; }
        public DbSet<Image> Images { get; set; }
        public DbSet<Condition_Type> Condition_Types { get; set; }
        public DbSet<Body_Type> Body_Types { get; set; }
        public DbSet<Fuel_Type> Fuel_Types { get; set;}
        public DbSet<Transmission_Type> Transmission_Types { get; set; }

        public DbSet<Car_Brand> Car_Brands{ get; set; }

        public System.Data.Entity.DbSet<AutoMarket.Models.City> Cities { get; set; }
        public DbSet<Blog> Blogs { get; set; }
    }
}