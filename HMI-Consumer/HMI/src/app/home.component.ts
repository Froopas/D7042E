import { Component, OnInit, Pipe, PipeTransform} from '@angular/core';
import { AppService } from './app.service';
import { HttpClient } from '@angular/common/http';
import { AnimationGroupPlayer } from '@angular/animations/src/players/animation_group_player';
import { Observable, Subscription } from 'rxjs';

@Component({
  templateUrl: './home.component.html',
})
export class HomeComponent {

  httpC: HttpClient = null;
  title = 'Demo';
  sub: Subscription;
  greeting = {};
  test:Array<Object> = [];
  sysData = {};

  constructor(private app: AppService, private http: HttpClient) {
    this.httpC = http;
    http.get('resource').subscribe(data => this.greeting = data);
    http.get("systems").subscribe(data => {
      this.test = data as Array<Object>;
      console.log(data);
    });
    this.sub = Observable.interval(10000)
      .subscribe((val) => { this.fetchdata() });
    
  }

  authenticated() { return this.app.authenticated; }

  fetchdata() {
    this.httpC.get("systems").subscribe(data => {
      this.test = data as Array<Object>;
      console.log(this.test);
    });
  }

}

@Pipe({name: 'keys'})
export class KeysPipe implements PipeTransform {
  transform(value, args:string[]) : any {
    let keys = [];
    for (let key in value) {
      keys.push({key: key, value: value[key]});
    }
    return keys;
  }
}
