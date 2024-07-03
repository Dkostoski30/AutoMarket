namespace AutoMarket.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class _25 : DbMigration
    {
        public override void Up()
        {
            AddColumn("dbo.Listings", "Car_Kilowatts", c => c.String(nullable: false));
        }
        
        public override void Down()
        {
            DropColumn("dbo.Listings", "Car_Kilowatts");
        }
    }
}
