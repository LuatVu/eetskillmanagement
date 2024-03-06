import { Injectable } from '@angular/core';
import { ProjectsService } from '../../projects.service';

@Injectable({
  providedIn: 'root'
})
export class DeleteProjectService {

  constructor(private projectsService: ProjectsService) { }
  deleteProject(id: string) {
    return this.projectsService.deleteProjects(id);
  }
}
