using Microsoft.Owin;
using Owin;

[assembly: OwinStartupAttribute(typeof(AutoMarket.Startup))]
namespace AutoMarket
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureAuth(app);
        }
    }
}
