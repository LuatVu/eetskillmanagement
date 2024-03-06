import {
  Component,  
  ChangeDetectionStrategy,
  OnChanges,
  Input,
  SimpleChanges,
  ViewEncapsulation,
} from '@angular/core';
import { SidebarNavItem, BreadcrumbsService, BciSidebarService } from '@bci-web-core/core';
import { MenuItem } from '../eet-header/menu-item.model';
import { BehaviorSubject, filter} from 'rxjs';
import { NavigationEnd, NavigationStart, Router } from '@angular/router';

@Component({
  selector: 'eet-app',
  templateUrl: './eet-app.component.html',
  styleUrls: ['./eet-app.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None
})
export class EetAppComponent implements OnChanges {
  @Input() applicationTitle = '';
  @Input()
  applicationTitleLineTwo!: string;
  @Input() sidebarLinks: SidebarNavItem[] = [];
  @Input() sidebarFooterLinks: SidebarNavItem[] = [];
  @Input() public menuItems: MenuItem[] = [];
  @Input() showSidebarFooterTime = false;

  public navigationItems: SidebarNavItem[] = [];


  private readonly previousLinksSubject = new BehaviorSubject<SidebarNavItem[]>([]);
  private count = 0;

  private isSameRootUrl(url1:any, url2: any): boolean{
    var result = true;
    var url1Arr = url1.split("/");
    var url2Arr = url2.split("/");
    if(url1Arr.length == url2Arr.length && url1Arr.length > 2){// because length = 2 is root url
      for(let index = 0; index < url1Arr.length - 1; index++){
        if(url1Arr[index] != url2Arr[index]){
          result = false;
          break;
        }
      }
    }else {
      var length = url1Arr.length < url2Arr.length?url1Arr.length:url2Arr.length;
      for(let index = 0; index < length; index++){
        if(url1Arr[index] != url2Arr[index]){
          result = false;
          break;
        }
      }
      
    }
    return result;
  } 

  constructor(private breadcrumbsService: BreadcrumbsService, private bciSidebarService: BciSidebarService, private router: Router) {
    this.bciSidebarService.getSidebarState().subscribe(() => {
      const next = this.sidebarLinks; 
      
      if(this.count < 1 ) {
        this.previousLinksSubject.next(next.map(d => ({...d, expanded: d.expanded})));
      } else {
        if(this.previousLinksSubject.getValue().length == 0 ){
          this.previousLinksSubject.next(next.map(d => ({...d, expanded: d.expanded})));
        } 
        const pre = this.previousLinksSubject.getValue();                
        
        if(this.count == 1){
          pre.forEach((preItem, i) => {
            const nextItem  = next[i];
  
            if(preItem.expanded === nextItem.expanded && preItem.active == true) {
              nextItem.expanded = false;
            }
          });  
        }else{
          pre.forEach((preItem, i) => {
            const nextItem  = next[i];
  
            if(preItem.expanded === nextItem.expanded) {
              nextItem.expanded = false;
            }
          }); 
        }              
        
        this.previousLinksSubject.next(next.map(d => ({...d, expanded: d.expanded})));
      }      
      this.count++;      
    });    

    this.router.events.pipe(
      filter((e: unknown) => {
        if(e instanceof NavigationStart){          
          var root = e.url;
          var url;
          if(root.indexOf('/eetportal/') > -1){
            url = root.substring(11);
          }else{
            url = root;
          }
                 
          const next = this.sidebarLinks;        
          next.forEach((element) => {
            if(this.isSameRootUrl(element.url, e.url)){
              element.expanded = true;              
            }else {
              element.expanded = false;
            }
          });
          this.previousLinksSubject.next(next.map(d => ({...d, expanded: d.expanded})));
          return false;          
        } else if(e instanceof NavigationEnd){          
          this.sidebarLinks.forEach((element) => {
            if(this.isSameRootUrl(element.url, e.url) 
              && e.url != e.urlAfterRedirects
              && element.items 
              && element.items.length > 0){
              element.items.forEach(ele => {
                if(ele.url == e.urlAfterRedirects){
                  ele.active = true;
                }else{
                  ele.active = false;
                }
              });
            }
          });
        }
        return false;
      }),
    ).subscribe(() => {
      this.count++;
      this.bciSidebarService.setSidebarState(true);      
    });
  }
  

  ngOnChanges(changes: SimpleChanges): void {

    if (changes['sidebarLinks'] || changes['sidebarFooterLinks']) {
      this.navigationItems = [...this.sidebarLinks, ...this.sidebarFooterLinks];
      if (this.navigationItems && this.navigationItems.length != 0) {
        this.breadcrumbsService.setNavigationItems(this.navigationItems);
      }
    }
  }
}
