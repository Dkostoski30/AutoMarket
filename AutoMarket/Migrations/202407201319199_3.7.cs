namespace AutoMarket.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class _37 : DbMigration
    {
        public override void Up()
        {
            AlterColumn("dbo.Blogs", "Title", c => c.String(nullable: false, maxLength: 100));
            AlterColumn("dbo.Blogs", "Content", c => c.String(nullable: false));
        }
        
        public override void Down()
        {
            AlterColumn("dbo.Blogs", "Content", c => c.String());
            AlterColumn("dbo.Blogs", "Title", c => c.String());
        }
    }
}
