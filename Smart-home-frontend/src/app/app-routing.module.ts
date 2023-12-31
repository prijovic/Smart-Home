import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import { HomePageComponent } from './components/home-page/home-page.component';
import { AuthGuard } from './auth/guards/auth.guard';
import { AdminGuard } from './auth/guards/admin.guard';

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    component: HomePageComponent,
  },
  {
    path: 'auth',
    loadChildren: () =>
      import('./auth/auth.module').then((module) => module.AuthModule),
  },
  {
    path: 'security',
    loadChildren: () =>
      import('./certificates/certificates.module').then(
        (module) => module.CertificatesModule
      ),
    canActivate: [AuthGuard],
  },
  {
    path: 'password',
    loadChildren: () =>
      import('./password/password.module').then(
        (module) => module.PasswordModule
      ),
  },
  {
    path: 'admin/security',
    loadChildren: () =>
      import('./certificates-admin/certificates-admin.module').then(
        (module) => module.CertificatesAdminModule
      ),
    canActivate: [AdminGuard],
  },
  {
    path: 'admin/users',
    loadChildren: () =>
      import('./users/users.module').then((module) => module.UsersModule),
    canActivate: [AdminGuard],
  },
  {
    path: 'admin/properties',
    loadChildren: () =>
      import('./properties/properties.module').then(
        (module) => module.PropertiesModule
      ),
    canActivate: [AdminGuard],
  },
  {
    path: 'report',
    loadChildren: () =>
      import('./report/report.module').then((module) => module.ReportModule),
    canActivate: [AuthGuard],
  },
  {
    path: 'properties',
    loadChildren: () =>
      import('./user-properties/user-properties.module').then(
        (module) => module.UserPropertiesModule
      ),
    canActivate: [AuthGuard],
  },
  {
    path: 'admin/alarms',
    loadChildren: () =>
      import('./alarms/alarms.module').then((module) => module.AlarmsModule),
    canActivate: [AdminGuard],
  },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules }),
  ],
  exports: [RouterModule],
})
export class AppRoutingModule {}
